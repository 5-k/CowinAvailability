package com.prateek.cowinAvailibility.service;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prateek.cowinAvailibility.configuration.AppConfiguration;
import com.prateek.cowinAvailibility.configuration.ReddisCacheConfig;
import com.prateek.cowinAvailibility.dto.MetricsDTO;
import com.prateek.cowinAvailibility.dto.cowinResponse.AvlResponse;
import com.prateek.cowinAvailibility.dto.cowinResponse.CowinResponse;
import com.prateek.cowinAvailibility.entity.Alerts;
import com.prateek.cowinAvailibility.entity.Metrics;
import com.prateek.cowinAvailibility.entity.Notifications;
import com.prateek.cowinAvailibility.repo.AlertRepo;
import com.prateek.cowinAvailibility.repo.MetricsRepo;
import com.prateek.cowinAvailibility.utility.Utils;

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

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private ReddisCacheConfig reddisCacheConfig;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private MetricsRepo metricsRepo;

    public void forceRunCron() {
        checkContiniousAVL();
    }

    //@Scheduled(cron = "${app.checkAVLCronJob}")
    public void checkContiniousAVL() {

        Date startTime = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(startTime);

        log.info("Cron Job to Notify Users at : " + cal.getTime());
        boolean isNightTime = checkIfNightTime(cal);
        if (isNightTime) {
            return;
        }

        List<Alerts> alerts = alertRepo.findByActiveTrue();

        if (null == alerts || alerts.size() == 0) {
            log.warn("No Alerts Setup, return empty ");
            return;
        }
        log.info("Run cron Job on alert size: " + alerts.size());
        ;
        MetricsDTO met = new MetricsDTO();

        for (Alerts alt : alerts) {
            Set<AvlResponse> processedResponse;
            CowinResponse res = getData(alt.isPinCodeSearch() ? alt.getPincode() : alt.getDistrictId(),
                    alt.isPinCodeSearch(), met);
            processedResponse = Utils.processResponse(alt, res, log);
            if (null != processedResponse && processedResponse.size() > 0) {
                met.incrementslotAvailableCount();
                checkAndNotify(alt, processedResponse, met);
            }
        }

        log.info("Metrics for this cron Job: " + met.toString());

        Date endDate = new Date();
        log.info("Method took : " + (endDate.getTime() - startTime.getTime()) + " MILLIS TO RUN");

        Metrics metric = Utils.fromMetricDto(met);
        metric.setStartTime(startTime);
        metric.setEndTime(endDate);
        metricsRepo.save(metric);
    }

    private boolean checkIfNightTime(Calendar cal) {
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);

        log.info("Current Hour: " + hour + " and minute: " + minutes);

        if ((hour > this.appConfiguration.getNightTimeStart()) || (hour < this.appConfiguration.getNightTimeEnd())) {
            log.info("Its night time, not sending notifications:");
            return true;
        }

        return false;
    }

    public CowinResponse getData(Integer districtOrPincode, boolean isPinCodeSearch, MetricsDTO met) {
        CowinResponse res = null;

        try {
            ValueOperations<String, String> cache = this.redisTemplate.opsForValue();
            String cacheKey = districtOrPincode.toString();
            String data = cache.get(cacheKey);
            if (null == data) {
                res = (isPinCodeSearch) ? asyncProcessor.checkByPinCode(districtOrPincode)
                        : asyncProcessor.checkAvlByDistrict(districtOrPincode);
                if (null == res) {
                    met.incrementDataNotLoaded();
                    log.error("Null response from districtorpincode" + districtOrPincode);
                    return null;
                }
                cache.set(cacheKey, mapper.writeValueAsString(res), reddisCacheConfig.getCacheExpiryInSeconds(),
                        TimeUnit.SECONDS);
                met.incrementDataLoadedFromAPI();
            } else {
                met.incrementDataLoadedFromCache();
                res = mapper.readValue(data, CowinResponse.class);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            met.incrementDataNotLoaded();
        }
        return res;
    }

    private void checkAndNotify(Alerts alt, Set<AvlResponse> res, MetricsDTO met) {
        if (CollectionUtils.isEmpty(alt.getNotifications()) || shouldNotify(alt)) {
            log.debug("Running for Alert: " + alt.getId());
            met.incrementNotificationEligibleCount();
            asyncProcessor.notifyUsers(alt, res);
        } else {
            log.debug("Should Not Notify: for Alert: " + alt.getId() + " with set size: " + res.size());
        }
    }

    public void refreshAvl(int id, boolean debug) {
        Optional<Alerts> alertVal = alertRepo.findById(id);
        if (alertVal.isPresent()) {
            Alerts alert = alertVal.get();
            CowinResponse response = asyncProcessor.getResponseForAlert(alert);

            if (null == response) {
                notificationService.sendTelegramUpdate(alert, "No vaccine available as per the alert: " + id, debug);
                return;
            }

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

        Calendar cal = Utils.getCalender();
        cal.setTime(new Date());

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
            log.debug("Already getMaxNotificationPerAlertPerDay notification issued to this mobile number:  for alert "
                    + alert.getId() + " Not issuing current one");
            return false;
        } else if (notificationSentToday > 0) {
            Notifications notification = alert.getNotifications().get(0); // Latest Notificiation
            Calendar latestNotification = Calendar.getInstance();

            if (latestNotification.get(Calendar.DATE) == cal.get(Calendar.DATE)
                    && latestNotification.get(Calendar.MONTH) == cal.get(Calendar.MONTH)
                    && latestNotification.get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
                long td = cal.getTime().getTime() - notification.getCreatedAt().getTime();
                long timeinMinutes = (td) / 1000 / 60;
                log.info("Last Notification sent at : " + notification.getCreatedAt() + " and current time is "
                        + cal.getTime() + " and their time difference in millis is " + td + " and in minutes is "
                        + timeinMinutes);

                if (timeinMinutes < appConfiguration.getTimeDifferenceBetweenPreviousNotificationInMins()) {
                    log.info("Max notification is 1 every "
                            + appConfiguration.getTimeDifferenceBetweenPreviousNotificationInMins()
                            + ", not sending notification for " + alert.getId());
                    return false;
                }
            }
        }
        return true;
    }
}
