package com.prateek.cowinAvailibility.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.prateek.cowinAvailibility.configuration.AppConfiguration;
import com.prateek.cowinAvailibility.dto.cowinResponse.AvlResponse;
import com.prateek.cowinAvailibility.dto.cowinResponse.CowinResponse;
import com.prateek.cowinAvailibility.dto.cowinResponse.CowinResponseCenter;
import com.prateek.cowinAvailibility.dto.cowinResponse.CowinResponseSessions;
import com.prateek.cowinAvailibility.entity.Alerts;
import com.prateek.cowinAvailibility.entity.Notifications;
import com.prateek.cowinAvailibility.repo.AlertRepo;
import com.prateek.cowinAvailibility.repo.NotificationsRepo;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CheckAvailivbilityService {

    public String checkByPincode;
    public String checkByDistrict;
    private SimpleDateFormat formatter;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AppConfiguration appConfiguration;

    @Autowired
    private AlertRepo alertRepo;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationsRepo notificationsRepo;

    // @Scheduled(cron = "0 0/1 * * * *")
    @Scheduled(cron = "0 * * * * *")
    @Async
    public void checkContiniousAVL() {
        List<Alerts> alerts = alertRepo.findByActiveTrue();
        log.info("Cron Job to check avl");

        for (int i = 0; i < alerts.size(); i++) {
            Alerts alert = alerts.get(i);
            CowinResponse response;

            if (alert.isPinCodeSearch()) {
                response = checkByPinCode(alert.getPincode());
            } else {
                response = checkAvlByDistrict(alert.getDistrictId());
            }

            processResponse(alert, response);
        }
    }

    @PostConstruct
    public void PostConstruct() {
        this.formatter = new SimpleDateFormat(this.appConfiguration.getDateFormat());
        this.checkByPincode = this.appConfiguration.getCheckByPincodeURL();
        this.checkByDistrict = this.appConfiguration.getCheckByDistrictURL();
    }

    public Set<AvlResponse> processResponse(Alerts alert, CowinResponse response) {
        Set<AvlResponse> avlResponseList = new LinkedHashSet<AvlResponse>();

        for (int i = 0; i < response.getCenters().size(); i++) {
            CowinResponseCenter center = response.getCenters().get(i);
            Set<CowinResponseSessions> validSessions = new LinkedHashSet<CowinResponseSessions>();

            for (int j = 0; j < center.getSessions().size(); j++) {

                CowinResponseSessions session = center.getSessions().get(j);
                if ((alert.getVaccineType().trim().equalsIgnoreCase("any")
                        || alert.getVaccineType().trim().equalsIgnoreCase(session.getVaccine()))
                        && session.getMin_age_limit() >= alert.getAge() && session.getAvailable_capacity() > 0) {
                    validSessions.add(session);
                } else {
                    log.info("Vaccination Type or age did not match or not available, ", session.toString());
                }
            }

            AvlResponse avlResponse = new AvlResponse(center.getCenter_id(), center.getName(), center.getAddress(),
                    center.getPincode(), validSessions);
            if (validSessions.size() > 0) {
                avlResponseList.add(avlResponse);
            }
        }

        if (avlResponseList.size() == 0) {
            log.debug("No avlResponseList Session found for Alert " + alert);
            return null;
        }

        notifyUsers(alert, avlResponseList);

        return avlResponseList;
    }

    private void notifyUsers(Alerts alert, Set<AvlResponse> avlResponseList) {
        List<Notifications> notifications = notificationsRepo.findByAlertId(alert.getId());
        if (null == notifications) {
            notifications = new ArrayList<>();
        }
        Collections.sort(notifications);
        Date currentDate = new Date();
        int count = 0;

        for (int i = 0; i < notifications.size(); i++) {
            Notifications notification = notifications.get(i);
            if (notification.getCreatedAt().getDate() == currentDate.getDate()
                    && notification.getCreatedAt().getMonth() == currentDate.getMonth()
                    && notification.getCreatedAt().getYear() == currentDate.getYear()) {
                count++;
            }
        }

        if (count >= this.appConfiguration.getMaxNotificationPerAlertPerDay()) {
            log.info("Already 2 notification issued to this mobile number:  for alert " + alert.toString()
                    + " Not issuing current one");
            return;
        }

        String alerts = alert.getNotificationType();
        if (null != alerts) {
            String alertList[] = alerts.split(",");
            for (int i = 0; i < alertList.length; i++) {
                notify(alert, avlResponseList, Integer.parseInt(alertList[i]));
            }
        }

        log.info("Successfully returning from Notifications");
    }

    private void notify(Alerts alert, Set<AvlResponse> avlResponseList, int notificationType) {
        String cost = "";

        switch (notificationType) {

            case 0:
                cost = notificationService.sendWhatsAppMessage(alert, avlResponseList);
                break;

            case 1:
                cost = notificationService.sendTestMessage(alert, avlResponseList);
                break;

            case 2:
                cost = notificationService.sendEmail(alert, avlResponseList);
                break;

            case 3:
                cost = notificationService.sendVaccineUpdates(alert, avlResponseList);
                break;
        }

        Notifications not = new Notifications(new Date(), alert.getPhoneNumber(), alert.getId(), cost,
                notificationType);
        notificationsRepo.save(not);

        log.info("Successfully returning from Notifications");
    }

    public CowinResponse checkAvlByDistrict(int districtId) {
        try {
            return getData(districtId, false);
        } catch (Exception e) {
            log.error("Exception fetching response by district for district id " + districtId, e);
            return null;
        }

    }

    public CowinResponse checkByPinCode(int pincode) {
        try {
            return getData(pincode, true);
        } catch (Exception e) {
            log.error("Exception fetching response by pincode for pincode id " + pincode, e);
            return null;
        }

    }

    private CowinResponse getData(int districtOrPincode, boolean isPinCode) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<String>(
                "parameters", headers);

        String formattedDate = formatter.format(new Date());
        String url = null;
        if (isPinCode) {
            url = checkByPincode.replace("{pinCode}", String.valueOf(districtOrPincode));
        } else {
            url = checkByDistrict.replace("{districtId}", String.valueOf(districtOrPincode));
        }

        url = url.replace("{dateVal}", formattedDate);
        ResponseEntity<CowinResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity,
                CowinResponse.class);

        return response.getBody();
    }
}
