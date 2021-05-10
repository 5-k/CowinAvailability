package com.prateek.cowinAvailibility.service.chatbot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import com.prateek.cowinAvailibility.configuration.AppConfiguration;
import com.prateek.cowinAvailibility.entity.Alerts;
import com.prateek.cowinAvailibility.repo.AlertRepo;
import com.prateek.cowinAvailibility.utility.HashMapCaseInsensitive;
import com.prateek.cowinAvailibility.utility.JsonResponse;
import com.prateek.cowinAvailibility.utility.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class CowinTelegramChatBot {

    private Map<String, String> actionResponseJson;
    Map<Long, Alerts> alertMap;
    Map<Long, String> previousQuestion;

    Map<String, Map<String, Integer>> cityMap;
    List<Map<String, Integer>> stateMap;

    @Autowired
    private AppConfiguration appConfiguration;

    @Autowired
    private AlertRepo alerRepo;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void loadResource() {
        try {
            alertMap = new HashMap<Long, Alerts>();
            previousQuestion = new HashMap<Long, String>();
            this.actionResponseJson = new ObjectMapper().readValue(new ClassPathResource("data.json").getFile(),
                    Map.class);
            this.stateMap = ((Map<String, List<Map<String, Integer>>>) new ObjectMapper()
                    .readValue(new ClassPathResource("stateList.json").getFile(), Map.class)).get("states");
            this.cityMap = ((Map<String, Map<String, Map<String, Integer>>>) (new ObjectMapper()
                    .readValue(new ClassPathResource("cityList.json").getFile(), Map.class))).get("districts");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<String> getResponseForMessage(String messageText, long chatId) {
        messageText = messageText.trim().toLowerCase();

        String response = actionResponseJson.get(messageText);
        List<String> responseList = new ArrayList<>();
        Alerts alert = this.alertMap.get(chatId);

        if (messageText.contains("stopupdatesforalert")) {
            String id = messageText.substring("stopupdatesforalert".length() + 1);
            Optional<Alerts> disableAlert = alerRepo.findById(Integer.parseInt(id));
            if (null != disableAlert && disableAlert.isPresent()) {
                Alerts alt = disableAlert.get();
                alt.setActive(false);
                alerRepo.save(alt);
            }
            responseList.add(actionResponseJson.get("disableAlert"));
            return responseList;
        } else if (messageText.contains("fetchlatestupdatefor")) {
            String id = messageText.substring("fetchlatestupdatefor".length() + 1);
            String url = appConfiguration.getAppHostNameURL() + "/app/availability/Alert/" + id;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add("user-agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<String>(
                    "parameters", headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<JsonResponse> res = restTemplate.exchange(url, HttpMethod.GET, entity, JsonResponse.class);
            log.info("Response, " + res.getStatusCode() + "- " + res.getBody());
            return responseList;
        }

        switch (messageText) {

            case "/stopUpdates":
                List<Alerts> alerts = alerRepo.findByPhoneNumber("telegram:" + chatId);
                if (null != alerts && alerts.size() > 0) {
                    for (int i = 0; i < alerts.size(); i++) {
                        alerts.get(i).setActive(false);
                    }
                }
                alerRepo.saveAll(alerts);
                responseList.add(actionResponseJson.get("disableAlert"));
                return responseList;

            case "/start":
            case "start":
                responseList.add(actionResponseJson.get(messageText));
                responseList.add(actionResponseJson.get("addOrDelete"));
                alert = new Alerts();
                this.alertMap.put(chatId, alert);
                this.previousQuestion.put(chatId, "/start");
                return responseList;

            case "/bye":
            case "bye":
                responseList.add(actionResponseJson.get(messageText));
                this.alertMap.put(chatId, null);
                this.previousQuestion.put(chatId, null);
                return responseList;

            default:
                if (null == alert) {
                    responseList.add(actionResponseJson.get("invalid_response"));
                    previousQuestion.put(chatId, "invalid_response");
                    return responseList;
                }
                break;
        }

        String prevQues = this.previousQuestion.get(chatId);
        if (null != prevQues) {
            switch (prevQues) {
                case "/start":
                    previousQuestion.put(chatId, "/addalert");
                    break;

                case "/addalert":
                case "addalert":
                case "add alert":
                case "/add alert":
                    alert.setName(messageText);
                    response = actionResponseJson.get("name_supplied");
                    previousQuestion.put(chatId, "name_supplied");
                    break;

                case "name_supplied":
                    previousQuestion.put(chatId, messageText);
                    if (messageText.equals("/state") || messageText.contains("state")) {
                        response = Utils.formatStateData(this.stateMap);
                        previousQuestion.put(chatId, "selectstate");
                    }
                    break;

                case "pincode":
                case "/pincode":
                case "invalidpincode":
                    boolean isValidPincode = Utils.isPinCodeValid(messageText);
                    if (isValidPincode) {
                        alert.setPinCodeSearch(true);
                        alert.setPincode(Integer.parseInt(messageText));
                        response = actionResponseJson.get("email_notifications");
                        previousQuestion.put(chatId, "email_notifications");
                    } else {
                        response = actionResponseJson.get("invalidpincode");
                        previousQuestion.put(chatId, "invalidpincode");
                    }
                    break;

                case "state":
                case "/state":
                    response = Utils.formatStateData(this.stateMap);
                    previousQuestion.put(chatId, "selectcity");
                    break;

                case "selectstate":
                case "/selectstate":
                    List<String> stateListV = Utils.getStateList(this.stateMap);

                    if (stateListV.contains(messageText)) {
                        Map<String, Map<String, Integer>> linkedHashMap = new HashMapCaseInsensitive<String, Map<String, Integer>>(
                                this.cityMap);
                        Map<String, Integer> districtMap = linkedHashMap.get(messageText);
                        response = Utils.formatCityData(districtMap);
                        previousQuestion.put(chatId, "selectcity");
                    } else {
                        // invalidstate
                        response = this.actionResponseJson.get("invalidstate");
                        previousQuestion.put(chatId, "selectstate");
                    }
                    break;

                case "selectcity":
                case "/selectcity":
                case "invalidcity":
                    Map<String, Map<String, Integer>> linkedHashMap = new HashMapCaseInsensitive<String, Map<String, Integer>>(
                            this.cityMap);
                    int districtId = Utils.getDistrictId(linkedHashMap, messageText);
                    if (districtId > 0) {
                        alert.setDistrictId(districtId);
                        alert.setPinCodeSearch(false);
                        response = actionResponseJson.get("email_notifications");
                        previousQuestion.put(chatId, "email_notifications");
                    } else {
                        response = actionResponseJson.get("invalidcity");
                        previousQuestion.put(chatId, "invalidcity");
                    }
                    break;

                case "email_notifications":
                case "/email_notifications":
                    if (messageText.contains("/noemail")) {
                        log.info(" No Email provided");
                    } else {
                        String notType = alert.getNotificationType();
                        if (null == notType || notType.length() == 0) {
                            notType = "2";
                        } else {
                            notType = notType + ",2";
                        }
                        alert.setNotificationType(notType);
                        alert.setEmail(messageText);
                    }

                    response = actionResponseJson.get("age");
                    previousQuestion.put(chatId, "age");
                    break;

                case "/NOEMAIL":
                    response = actionResponseJson.get("age");
                    previousQuestion.put(chatId, "age");
                    break;

                case "age":
                    if (messageText.contains("18to44")) {
                        alert.setAge(18);
                    } else if (messageText.contains("/45+") || messageText.contains("45")) {
                        alert.setAge(45);
                    }
                    response = actionResponseJson.get("success");
                    previousQuestion.put(chatId, "success");
                    alert.setCreatedAt(new Date());
                    alert.setModifiedAt(new Date());

                    String notType = alert.getNotificationType();
                    if (null == notType || notType.length() == 0) {
                        notType = "3";
                    } else {
                        notType = notType + ",3";
                    }
                    alert.setNotificationType(notType);
                    alert.setPhoneNumber("telegram:" + chatId);
                    alert.setActive(true);
                    alert.setVaccineType("any");
                    alerRepo.save(alert);
                    break;

                case "success":
                    previousQuestion.put(chatId, "");
                    break;

                case "/deletealert":
                case "delete alert":
                case "/delete alert":

                    alert.setName(messageText);
                    response = actionResponseJson.get("email_notifications");
                    previousQuestion.put(chatId, "email_notifications");
                    break;

            }

        } else {
            this.previousQuestion.put(chatId, "/start");
            this.alertMap.put(chatId, new Alerts());
        }

        if (null == response) {
            responseList.add(actionResponseJson.get("invalid_response"));
            return responseList;
        } else {
            responseList.add(response);
        }

        return responseList;
    }

}
