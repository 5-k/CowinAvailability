package com.prateek.cowinAvailibility.controller.secured;

import com.prateek.cowinAvailibility.dto.AlertDTO;
import com.prateek.cowinAvailibility.service.Service;
import com.prateek.cowinAvailibility.utility.JsonResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.*;

@RestController
@RequestMapping("/app/alert")
public class AlertController {

    @Autowired
    private Service service;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/Alerts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Update registration detail", authorizations = { @Authorization(value = "basicAuth") })
    public ResponseEntity<?> getAlerts() {
        log.info("Rest to fetch all alerts");

        try {
            return new ResponseEntity(service.getAlerts(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred : {} ", e.getMessage(), e);
            return new ResponseEntity<JsonResponse>(new JsonResponse("Exception adding alert"),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/Alerts/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAlerts(@PathVariable int id) {
        log.info("Rest to fetch alerts by id " + id);

        try {
            return new ResponseEntity(service.getAlertsById(id), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred : {} ", e.getMessage(), e);
            return new ResponseEntity<JsonResponse>(new JsonResponse("Exception adding alert"),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/Alerts/phone", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAlertsByPhone(@RequestBody AlertDTO aDto) {
        log.info("Rest to fetch user alerts for ", aDto.toString());

        try {
            return new ResponseEntity(service.getAlertByMobileNumber(aDto), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred : {} ", e.getMessage(), e);
            return new ResponseEntity<JsonResponse>(new JsonResponse("Exception adding alert"),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/Alert", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addAlert(@RequestBody AlertDTO aDto) {

        try {
            log.info("Rest for signing up new User");
            return new ResponseEntity(service.addAlert(aDto), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred : {} ", e.getMessage(), e);
            return new ResponseEntity<JsonResponse>(new JsonResponse("Exception adding alert"),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    // Remove Alert
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/Alerts/delete/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> disableAlertById(@PathVariable int id) {
        removeAlertById(id);
        return new ResponseEntity(
                "You have been successfully removed from the alert list. Thankyou and have a good day!", HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/Alerts/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> removeAlertById(@PathVariable int id) {
        log.info("Rest to disable alerts by id " + id);

        try {
            return new ResponseEntity(service.removeAlertById(id), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred : {} ", e.getMessage(), e);
            return new ResponseEntity<JsonResponse>(new JsonResponse("Exception adding alert"),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/Alerts/phone", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> removeAlertsByPhone(@RequestBody AlertDTO aDto) {
        log.info("Rest to disable user alerts by phone for ", aDto.toString());

        try {
            return new ResponseEntity(service.removeAlertByPhone(aDto.getPhoneNumber()), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred : {} ", e.getMessage(), e);
            return new ResponseEntity<JsonResponse>(new JsonResponse("Exception adding alert"),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/Alerts/sendWelcomeEmail/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendWelcomeEmail(@PathVariable int id) {
        service.sendWelcomeEmail(id);
        return new ResponseEntity("Email Sent!", HttpStatus.OK);
    }

}
