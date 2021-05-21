package com.prateek.cowinAvailibility.service;

import java.util.Date;
import java.util.Set;

import com.prateek.cowinAvailibility.dto.cowinResponse.AvlResponse;
import com.prateek.cowinAvailibility.entity.Alerts;
import com.prateek.cowinAvailibility.entity.Notifications;
import com.prateek.cowinAvailibility.repo.AlertRepo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("generateNotificationService")
public class GenerateNotificationService implements IGenerateNotificationService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AlertRepo alertRepo;

    public void notifyUsers(Alerts alert, Set<AvlResponse> avlResponseList) {
        Date currentDate = new Date();

        String alerts = alert.getNotificationType();
        if (null != alerts) {
            String alertList[] = alerts.split(",");
            for (int i = 0; i < alertList.length; i++) {
                notify(alert, avlResponseList, Integer.parseInt(alertList[i]), currentDate);
            }
        }

        log.info("Successfully returning from Notifications");
    }

    private void notify(Alerts alert, Set<AvlResponse> avlResponseList, int notificationType, Date date) {
        String cost = "";
        log.info("Notifaction for Alert for notification type: " + notificationType);
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
            cost = notificationService.sendTelegramMessage(alert, avlResponseList, false);
            break;
        }

        if (null == cost) {
            log.warn("Notification not sent because of less seats!");
            return;
        }

        Notifications not2 = new Notifications(date, alert.getPhoneNumber(), alert, cost, notificationType);
        alert.getNotifications().add(not2);
        alertRepo.save(alert);
        log.info("Notifiication saved for alert: " + alert.getId());
    }
}
