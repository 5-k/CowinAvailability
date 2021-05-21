package com.prateek.cowinAvailibility.service.chatbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;
import com.prateek.cowinAvailibility.configuration.AppConfiguration;
import com.prateek.cowinAvailibility.dto.cowinResponse.AvlResponse;
import com.prateek.cowinAvailibility.dto.cowinResponse.CowinResponse;
import com.prateek.cowinAvailibility.entity.Alerts;
import com.prateek.cowinAvailibility.entity.Feedback;
import com.prateek.cowinAvailibility.repo.AlertRepo;
import com.prateek.cowinAvailibility.repo.FeedbackRepo;
import com.prateek.cowinAvailibility.service.DataService;
import com.prateek.cowinAvailibility.service.ExternalService;
import com.prateek.cowinAvailibility.utility.Constants;
import com.prateek.cowinAvailibility.utility.HashMapCaseInsensitive;
import com.prateek.cowinAvailibility.utility.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;
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

    @Autowired
    private DataService dataService;

    @Autowired
    private FeedbackRepo feedbackRepo;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ExternalService externalService;

    @PostConstruct
    public void loadResource() {
        alertMap = new HashMap<Long, Alerts>();
        previousQuestion = new HashMap<Long, String>();
        this.actionResponseJson = dataService.getActionResponseJson();
        this.stateMap = dataService.getStateMap();
        this.cityMap = dataService.getCityMap();

    }

    public List<String> getResponseForMessage(String messageText, long chatId) {
        messageText = messageText.trim().toLowerCase();

        String response = actionResponseJson.get(messageText);
        log.debug("Getting mapping for " + messageText + " - " + response);

        List<String> responseList = new ArrayList<>();
        Alerts alert = this.alertMap.get(chatId);
        String stringChatId = String.valueOf(chatId);

        if (messageText.contains("stopupdatesforalert")) {
            String id = messageText.substring("stopupdatesforalert".length() + 1);
            Optional<Alerts> disableAlert = alerRepo.findById(Integer.parseInt(id));
            if (null == disableAlert || !disableAlert.isPresent()) {
                responseList.add(actionResponseJson.get("nosuchalert"));
                return responseList;
            }

            Alerts alertVal = disableAlert.get();
            if (alertVal.getPhoneNumber().contains(stringChatId)
                    || stringChatId.equals(appConfiguration.getDebugTelegramChatId())) {
                Alerts alt = disableAlert.get();
                alt.setActive(false);
                alt.setModifiedAt(Utils.getCurrentDate());
                alerRepo.save(alt);
                responseList.add(actionResponseJson.get("deletesuccess"));
                responseList
                        .add("\nTo resume updated for this alert select **/resumeUpdatesForAlert" + alt.getId() + "**");
                responseList.add("To view alerts **/viewAlerts****");
            } else {
                responseList.add(actionResponseJson.get("deleteyouralertsonly"));
            }
            responseList.add(actionResponseJson.get("/getFeedback"));
            responseList.add(actionResponseJson.get("feedback"));
            this.previousQuestion.put(chatId, "/feedback");
            return responseList;
        } else if (messageText.contains("resumeupdatesforalert")) {
            String id = messageText.substring("resumeupdatesforalert".length() + 1);
            Optional<Alerts> enableAlert = alerRepo.findById(Integer.parseInt(id));
            if (null == enableAlert || !enableAlert.isPresent()) {
                responseList.add(actionResponseJson.get("nosuchalert"));
                return responseList;
            }

            Alerts alertVal = enableAlert.get();
            if (alertVal.getPhoneNumber().contains(stringChatId)
                    || stringChatId.equals(appConfiguration.getDebugTelegramChatId())) {
                Alerts alt = enableAlert.get();
                alt.setActive(true);
                alt.setModifiedAt(Utils.getCurrentDate());
                alerRepo.save(alt);
                responseList.add(actionResponseJson.get("activesuccess"));
            } else {
                responseList.add(actionResponseJson.get("reactivateyouralertsonly"));
            }
            responseList.add(actionResponseJson.get("/getFeedback"));
            responseList.add(actionResponseJson.get("feedback"));
            this.previousQuestion.put(chatId, "/feedback");
            return responseList;
        } else if (messageText.contains("fetchlatestupdatefor")) {
            int id = Integer.parseInt(messageText.substring("fetchlatestupdatefor".length() + 1));
            Optional<Alerts> alt = alerRepo.findById(id);
            if (null == alt || !alt.isPresent()) {
                responseList.add(actionResponseJson.get("nosuchalert"));
                return responseList;
            }
            Alerts alertVal = alt.get();
            CowinResponse res;
            if (alertVal.isPinCodeSearch()) {
                res = externalService.getData(alertVal.getPincode(), true);
            } else {
                res = externalService.getData(alertVal.getDistrictId(), false);
            }

            if (null == res) {
                responseList.add("No vaccine available as per the alert: " + alertVal.getId());
                return responseList;
            }

            Set<AvlResponse> avlResponseList = Utils.processResponse(alertVal, res, log);
            if (avlResponseList.isEmpty()) {
                responseList.add("Currently no vaccine is available as per the for Alert: " + alertVal.getId());
            } else {
                responseList.add(Utils.getTelegramAlertMessage(alertVal, avlResponseList, true));
            }
            return responseList;
        }

        switch (messageText) {

        case "/stopupdates": {
            List<Alerts> alerts = alerRepo.findByPhoneNumber("telegram:" + chatId);
            if (null != alerts && alerts.size() > 0) {
                for (Alerts alt : alerts) {
                    alt.setActive(false);
                    alt.setModifiedAt(Utils.getCurrentDate());
                }
            }

            alerRepo.saveAll(alerts);
            responseList.add(actionResponseJson.get("disableAlert"));
            responseList.add(actionResponseJson.get("/getFeedback"));
            responseList.add(actionResponseJson.get("feedback"));
            this.previousQuestion.put(chatId, "/feedback");
            return responseList;
        }

        case "/resumeupdates": {
            List<Alerts> alerts = alerRepo.findByPhoneNumber("telegram:" + chatId);
            if (null != alerts && alerts.size() > 0) {
                for (Alerts alt : alerts) {
                    alt.setActive(true);
                    alt.setModifiedAt(Utils.getCurrentDate());
                }
            }

            alerRepo.saveAll(alerts);
            responseList.add(actionResponseJson.get("disableAlert"));
            responseList.add(actionResponseJson.get("/getFeedback"));
            responseList.add(actionResponseJson.get("feedback"));
            this.previousQuestion.put(chatId, "/feedback");
            return responseList;
        }

        case "/feedback":
            if (this.alertMap.containsKey(chatId)) {
                this.alertMap.remove(chatId);
            }
            responseList.add(actionResponseJson.get("/feedback"));
            this.previousQuestion.put(chatId, "/feedback");
            return responseList;

        case "/contact":
            if (this.alertMap.containsKey(chatId)) {
                this.alertMap.remove(chatId);
            }
            responseList.add(actionResponseJson.get("/contact"));
            this.previousQuestion.put(chatId, null);
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
            if (this.alertMap.containsKey(chatId)) {
                this.alertMap.remove(chatId);
            }
            if (this.previousQuestion.containsKey(chatId)) {
                this.previousQuestion.remove(chatId);
            }

            return responseList;

        case "/viewalerts":
            List<Alerts> alertList = alerRepo.findByPhoneNumberContainingAndActiveTrue(stringChatId);
            if (null == alertList || alertList.size() == 0) {
                responseList.add(actionResponseJson.get("noalertssetontelegram"));
                return responseList;
            }
            responseList.add(actionResponseJson.get("alertslistedbelow"));
            for (int i = 0; i < alertList.size(); i++) {
                Alerts alt = alertList.get(i);
                StringBuilder builder = new StringBuilder();
                builder.append("Name: ");
                builder.append(alt.getName());
                builder.append("\nAge Group: ");
                builder.append(alt.getAge()).append("+");
                builder.append("\nLocation: ");
                builder.append(alt.isPinCodeSearch() ? alt.getPincode() : alt.getCity() + "," + alt.getState());
                builder.append("\nVaccineType: ");
                builder.append(StringUtils.capitalize(alt.getVaccineType()));
                builder.append("\nDoseType: ");
                builder.append((alt.getDoseageType() == 0) ? "Both" : "Dose " + alt.getDoseageType());

                builder.append("\n\n");
                builder.append("Fetch Latest update for this alert for **/fetchlatestupdatefor").append(alt.getId())
                        .append("**");
                builder.append("\n\nDisable alert for this alert for **/stopupdatesforalert").append(alt.getId())
                        .append("**");
                builder.append("\n\n");

                responseList.add(builder.toString());
            }
            responseList.add(actionResponseJson.get("/addOrDelete"));
            return responseList;

        default:
            if (null != this.previousQuestion.get(chatId) && this.previousQuestion.get(chatId).equals("/feedback")) {
                if (messageText.startsWith("/")) {
                    responseList.add(actionResponseJson.get("/invalidFeedback"));
                    responseList.add(actionResponseJson.get("/feedback"));
                    previousQuestion.put(chatId, "/feedback");
                    return responseList;
                }

                Feedback feedback = new Feedback("telegram:" + String.valueOf(chatId), messageText,
                        Utils.getCurrentDate());
                feedbackRepo.save(feedback);
                if (this.alertMap.containsKey(chatId)) {
                    this.alertMap.remove(chatId);
                }
                if (this.previousQuestion.containsKey(chatId)) {
                    this.previousQuestion.remove(chatId);
                }
                responseList.add(actionResponseJson.get("successfeedback"));
                return responseList;
            } else if (null == alert) {
                responseList.add(actionResponseJson.get("invalid_response"));
                previousQuestion.put(chatId, "invalid_response");
                return responseList;
            }
            break;
        }

        String prevQues = this.previousQuestion.get(chatId);
        if (null != prevQues) {
            switch (prevQues) {

            case "/feedback":
            case "/start":
                previousQuestion.put(chatId, "/addalert");
                break;

            case "/addalert":
            case "addalert":
            case "add alert":
            case "/add alert":
                if (messageText.startsWith("/") || messageText.length() < 3) {
                    responseList.add(actionResponseJson.get("/invalidname"));
                    responseList.add(actionResponseJson.get("/addalert"));
                    previousQuestion.put(chatId, "/addalert");
                    return responseList;
                }
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

                for (String state : stateListV) {
                    if (state.contains(messageText)) {
                        log.info("Mapping state " + state + " to " + messageText);
                        messageText = state;
                        break;
                    }
                }

                if (stateListV.contains(messageText) || stateListV.contains("/".concat(messageText))) {
                    Map<String, Map<String, Integer>> linkedHashMap = new HashMapCaseInsensitive<String, Map<String, Integer>>(
                            this.cityMap);
                    if (messageText.startsWith("/")) {
                        alert.setState(StringUtils.capitalize(messageText.substring(1)));
                    } else {
                        alert.setState(StringUtils.capitalize(messageText.substring(1)));
                    }

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

                if (!messageText.startsWith("/")) {
                    messageText = "/".concat(messageText);
                }

                int districtId = Utils.getDistrictId(linkedHashMap, messageText);
                if (districtId > 0) {
                    alert.setCity(StringUtils.capitalize(messageText.substring(1)));
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
                if (messageText.equalsIgnoreCase("/skipemailnotification")) {
                    log.info(" No Email provided");
                } else if (messageText.startsWith("/") || messageText.indexOf(".") == -1
                        || messageText.indexOf("@") == -1) {
                    previousQuestion.put(chatId, "/email_notifications");
                    responseList.add(actionResponseJson.get("/invalidEmail"));
                    return responseList;
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

            case "/skipemailnotification":
                response = actionResponseJson.get("age");
                previousQuestion.put(chatId, "age");
                break;

            case "age":
                if (messageText.contains("18to44")) {
                    alert.setAge(18);
                } else if (messageText.contains("/45+") || messageText.contains("45")) {
                    alert.setAge(45);
                } else {
                    responseList.add(actionResponseJson.get("invalidageselection"));
                    responseList.add(actionResponseJson.get("age"));
                    previousQuestion.put(chatId, "age");
                    return responseList;
                }
                response = actionResponseJson.get("vaccinechoice");
                previousQuestion.put(chatId, "vaccinechoice");
                responseList.add(response);
                return responseList;

            case "vaccinechoice":
            case "/vaccinechoice":
            case "vaccine choice":
                if (messageText.contains("covaxin")) {
                    alert.setVaccineType(Constants.VACCINE_TYPE_COVAXIN);
                } else if (messageText.contains("covishield")) {
                    alert.setVaccineType(Constants.VACCINE_TYPE_COVISHIELD);
                } else if (messageText.contains("sputnik")) {
                    alert.setVaccineType(Constants.VACCINE_TYPE_SPUTNIK);
                } else if (messageText.contains("any")) {
                    alert.setVaccineType(Constants.VACCINE_TYPE_ANY);
                } else {
                    responseList.add(actionResponseJson.get("invalidvaccinetype"));
                    responseList.add(actionResponseJson.get("vaccinechoice"));
                    previousQuestion.put(chatId, "/vaccinetype");
                    return responseList;
                }

                if (alert.getVaccineType().equals(Constants.VACCINE_TYPE_SPUTNIK)) {
                    response = actionResponseJson.get("success");
                    previousQuestion.put(chatId, "success");
                    alert.setCreatedAt(Utils.getCurrentDate());
                    alert.setModifiedAt(Utils.getCurrentDate());

                    String notType = alert.getNotificationType();
                    if (null == notType || notType.length() == 0) {
                        notType = "3";
                    } else {
                        notType = notType + ",3";
                    }
                    alert.setNotificationType(notType);
                    alert.setPhoneNumber("telegram:" + chatId);
                    alert.setActive(true);
                    alerRepo.save(alert);
                } else {
                    responseList.add(actionResponseJson.get("dosetype"));
                    previousQuestion.put(chatId, "dosetype");
                    return responseList;
                }
                break;

            case "dosetype":
            case "/dosetype":
            case "dose type":
                if (messageText.contains("firstdose")) {
                    alert.setDoseageType(1);
                } else if (messageText.contains("seconddose")) {
                    alert.setDoseageType(2);
                } else if (messageText.contains("both")) {
                    alert.setDoseageType(0);
                } else {
                    responseList.add(actionResponseJson.get("invaliddosetype"));
                    responseList.add(actionResponseJson.get("dosetype"));
                    previousQuestion.put(chatId, "/dosetype");
                    return responseList;
                }

                response = actionResponseJson.get("success");
                previousQuestion.put(chatId, "success");
                alert.setCreatedAt(Utils.getCurrentDate());
                alert.setModifiedAt(Utils.getCurrentDate());

                String notType = alert.getNotificationType();
                if (null == notType || notType.length() == 0) {
                    notType = "3";
                } else {
                    notType = notType + ",3";
                }
                alert.setNotificationType(notType);
                alert.setPhoneNumber("telegram:" + chatId);
                alert.setActive(true);
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
