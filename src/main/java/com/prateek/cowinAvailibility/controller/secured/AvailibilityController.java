package com.prateek.cowinAvailibility.controller.secured;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.prateek.cowinAvailibility.service.CheckAvailivbilityService;
import com.prateek.cowinAvailibility.service.IAsyncProcessor;
import com.prateek.cowinAvailibility.utility.JsonResponse;

@RestController
@RequestMapping("/app/availability")
public class AvailibilityController {

    @Autowired
    private CheckAvailivbilityService service;

    @Autowired
    @Qualifier("AsyncProcessor")
    private IAsyncProcessor asyncProcessor;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/district/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAvlByDistrictId(@PathVariable int id) {
        log.info("Rest to availability by district id for id " + id);

        try {
            return new ResponseEntity(asyncProcessor.checkAvlByDistrict(id), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred : {} ", e.getMessage(), e);
            return new ResponseEntity<JsonResponse>(new JsonResponse("Exception fethcing data"),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/pincode/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAvlByPinCode(@PathVariable int id) {
        log.info("Rest to getAvlByPinCode by pincode id for id " + id);

        try {
            return new ResponseEntity(asyncProcessor.checkByPinCode(id), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred : {} ", e.getMessage(), e);
            return new ResponseEntity<JsonResponse>(new JsonResponse("Exception fethcing data"),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/forceCrone/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> forceRunCroneJob() {
        log.info("Force Run Cron Job");
        service.forceRunCron();
        return new ResponseEntity(new JsonResponse("Force Run Success"), HttpStatus.OK);
    }
}
