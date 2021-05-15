package com.prateek.cowinAvailibility.controller.publicAPI;

import com.prateek.cowinAvailibility.service.CheckAvailivbilityService;
import com.prateek.cowinAvailibility.service.IAsyncProcessor;
import com.prateek.cowinAvailibility.utility.JsonResponse;

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

@RestController
@RequestMapping("/api")
public class UnSecuredController {
    @Autowired
    private CheckAvailivbilityService service;

    @Autowired
    @Qualifier("AsyncProcessor")
    private IAsyncProcessor asyncProcessor;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/availability/Alert/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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
    @RequestMapping(value = "/availabilityDebug/Alert/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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
}
