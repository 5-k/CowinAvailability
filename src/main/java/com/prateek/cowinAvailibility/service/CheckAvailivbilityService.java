package com.prateek.cowinAvailibility.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import com.prateek.cowinAvailibility.dto.cowinResponse.AvlResponse;
import com.prateek.cowinAvailibility.dto.cowinResponse.CowinResponse;
import com.prateek.cowinAvailibility.entity.Alerts;
import com.prateek.cowinAvailibility.repo.AlertRepo;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
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
            log.debug("Running for Alert: " + alert.toString());
            asyncProcessor.process(alert);
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

}
