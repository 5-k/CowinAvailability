package com.prateek.cowinAvailibility.service;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.prateek.cowinAvailibility.dto.cowinResponse.AvlResponse;
import com.prateek.cowinAvailibility.dto.cowinResponse.CowinResponse;
import com.prateek.cowinAvailibility.entity.Alerts;
import com.prateek.cowinAvailibility.service.chatbot.ITelegramSlotPoller;
import com.prateek.cowinAvailibility.utility.Utils;

@Service("AsyncProcessor")
public class AsyncProcessor implements IAsyncProcessor {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("generateNotificationService")
    private IGenerateNotificationService generateNotificationService;

    @Autowired
    @Qualifier("telegramSlotPoller")
    private ITelegramSlotPoller service;

    @Autowired
    private ExternalService externalService;

    @Async
    @Override
    public void processAndNotify(Alerts alert) {
        CowinResponse response = getResponseForAlert(alert);
        log.debug("Got response from cowin");

        if (null != response) {
            Set<AvlResponse> avlResponseList = processResponse(alert, response);
            log.warn("List AvlResponse size = " + avlResponseList.size());

            if (avlResponseList.size() > 0) {
                notifyUsers(alert, avlResponseList);
            } else {
                log.info("Nothing to notify ");
            }
        } else {
            log.error("Response is null");
        }

    }

    @Override
    public Set<AvlResponse> processResponse(Alerts alert, CowinResponse response) {
        return Utils.processResponse(alert, response, log);
    }

    @Async
    @Override
    public void notifyUsers(Alerts alert, Set<AvlResponse> avlResponseList) {
        generateNotificationService.notifyUsers(alert, avlResponseList);
    }

    @Override
    public CowinResponse getResponseForAlert(Alerts alert) {
        CowinResponse response;

        if (alert.isPinCodeSearch()) {
            response = checkByPinCode(alert.getPincode());
        } else {
            response = checkAvlByDistrict(alert.getDistrictId());
        }
        return response;
    }

    @Override
    public CowinResponse checkAvlByDistrict(int districtId) {
        try {
            return externalService.getData(districtId, false);
        } catch (Exception e) {
            log.error("Exception fetching response by district for district id " + districtId, e);
            return null;
        }

    }

    @Override
    public CowinResponse checkByPinCode(int pincode) {
        try {
            return externalService.getData(pincode, true);
        } catch (Exception e) {
            log.error("Exception fetching response by pincode for pincode id " + pincode, e);
            return null;
        }
    }

}
