package com.prateek.cowinAvailibility.controller;

import com.prateek.cowinAvailibility.service.CheckAvailivbilityService;
import com.prateek.cowinAvailibility.utility.JsonResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@org.springframework.web.bind.annotation.RestController
public class AvailibilityController {

    @Autowired
    private CheckAvailivbilityService service;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/app/availability/district/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAvlByDistrictId(@PathVariable int id) {
        log.info("Rest to availability by district id for id " + id);

        try {
            return new ResponseEntity(service.checkAvlByDistrict(id), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred : {} ", e.getMessage(), e);
            return new ResponseEntity<JsonResponse>(new JsonResponse("Exception fethcing data"),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/app/availability/pincode/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAvlByPinCode(@PathVariable int id) {
        log.info("Rest to getAvlByPinCode by pincode id for id " + id);

        try {
            return new ResponseEntity(service.checkByPinCode(id), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred : {} ", e.getMessage(), e);
            return new ResponseEntity<JsonResponse>(new JsonResponse("Exception fethcing data"),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/app/availability/Alert/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> fetchAlertData(@PathVariable int id) {
        log.info("Refresh /app/availability/Alert/" + id);

        try {
            service.refreshAvl(id, false);
            return new ResponseEntity(new JsonResponse("Success"), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred : {} ", e.getMessage(), e);
            return new ResponseEntity<JsonResponse>(new JsonResponse("Exception updating alert"),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/app/availabilityDebug/Alert/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> fetchAlertDataDebug(@PathVariable int id) {
        log.info("Refresh /app/availability/Alert/" + id);

        try {
            service.refreshAvl(id, true);
            return new ResponseEntity(new JsonResponse("Success"), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred : {} ", e.getMessage(), e);
            return new ResponseEntity<JsonResponse>(new JsonResponse("Exception updating alert"),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/app/forceCrone/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> forceRunCroneJob() {
        log.info("Force Run Cron Job");
        service.forceRunCron();
        return new ResponseEntity(new JsonResponse("Force Run Success"), HttpStatus.OK);
    }
}
