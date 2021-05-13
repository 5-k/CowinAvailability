package com.prateek.cowinAvailibility.service;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.prateek.cowinAvailibility.configuration.AppConfiguration;
import com.prateek.cowinAvailibility.dto.cowinResponse.AvlResponse;
import com.prateek.cowinAvailibility.dto.cowinResponse.CowinResponse;
import com.prateek.cowinAvailibility.entity.Alerts;
import com.prateek.cowinAvailibility.entity.Notifications;
import com.prateek.cowinAvailibility.repo.AlertRepo;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CheckAvailivbilityService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AlertRepo alertRepo;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    @Qualifier("generateNotificationService")
    private IGenerateNotificationService generateNotificationService;

    @Autowired
    @Qualifier("AsyncProcessor")
    private IAsyncProcessor asyncProcessor;

    @Autowired
    private AppConfiguration appConfiguration;

    public void forceRunCron() {
        checkContiniousAVL();
    }

    @Scheduled(cron = "${app.checkAVLCronJob}")
    @Async
    public void checkContiniousAVL() {
        log.info("Cron Job to check avl");
        List<Alerts> alerts = alertRepo.findByActiveTrue();

        if (null == alerts || alerts.size() == 0) {
            log.warn("No Alerts Setup, return empty ");
            return;
        }

        log.warn("Cron running for Alert count: " + alerts.size());

        for (int i = 0; i < alerts.size(); i++) {
            Alerts alert = alerts.get(i);
            if (CollectionUtils.isEmpty(alert.getNotifications())) {
                log.debug("Running for Alert: " + alert.toString());
                asyncProcessor.processAndNotify(alert);
                continue;
            }

            if (shouldNotify(alert)) {
                asyncProcessor.processAndNotify(alert);
            } else {
                log.debug("Running for Alert: " + alert.toString());
            }

        }
    }

    public void refreshAvl(int id, boolean debug) {
        Optional<Alerts> alertVal = alertRepo.findById(id);
        if (alertVal.isPresent()) {
            Alerts alert = alertVal.get();
            CowinResponse response = asyncProcessor.getResponseForAlert(alert);
            Set<AvlResponse> avlResponseList = asyncProcessor.processResponse(alert, response);
            if (avlResponseList.isEmpty()) {
                notificationService.sendTelegramUpdate(alert,
                        "Currently no vaccine is available as per the for Alert: " + id, debug);
            } else {
                notificationService.sendTelegramMessage(alert, avlResponseList, debug);
            }
        }
    }

    private boolean shouldNotify(Alerts alert) {
        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.DATE, 0);
        Collections.sort(alert.getNotifications());
        int notificationSentToday = 0;

        for (Notifications not : alert.getNotifications()) {
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(not.getCreatedAt());

            if (cal2.get(Calendar.DATE) == cal.get(Calendar.DATE) && cal2.get(Calendar.MONTH) == cal.get(Calendar.MONTH)
                    && cal2.get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
                notificationSentToday++;
            }
        }

        if (notificationSentToday >= this.appConfiguration.getMaxNotificationPerAlertPerDay()) {
            log.info("Already getMaxNotificationPerAlertPerDay notification issued to this mobile number:  for alert "
                    + alert.toString() + " Not issuing current one");
            return false;
        } else if (notificationSentToday > 0) {
            Notifications notification = alert.getNotifications().get(0); // Latest Notificiation
            Calendar latestNotification = Calendar.getInstance();

            if (latestNotification.get(Calendar.DATE) == cal.get(Calendar.DATE)
                    && latestNotification.get(Calendar.MONTH) == cal.get(Calendar.MONTH)
                    && latestNotification.get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
                long td = currentDate.getTime() - notification.getCreatedAt().getTime();
                long timeinMinutes = (td) / 1000 / 60;
                log.info("Last Notification sent at : " + notification.getCreatedAt() + " and current time is "
                        + cal.getTime() + " and their time difference in millis is " + td + " and in minutes is "
                        + timeinMinutes);

                if (timeinMinutes < appConfiguration.getTimeDifferenceBetweenPreviousNotificationInMins()) {
                    log.info("Max notification is 1 every "
                            + appConfiguration.getTimeDifferenceBetweenPreviousNotificationInMins()
                            + ", not sending notification for " + alert);
                    return false;
                }
            }
        }
        return true;
    }
}
