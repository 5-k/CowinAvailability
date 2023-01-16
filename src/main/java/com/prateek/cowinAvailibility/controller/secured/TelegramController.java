package com.prateek.cowinAvailibility.controller.secured;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prateek.cowinAvailibility.service.TelegramMessagingService;
import com.prateek.cowinAvailibility.utility.JSONRequestMultiple;
import com.prateek.cowinAvailibility.utility.JSONResponseMultiple;
import com.prateek.cowinAvailibility.utility.JsonResponse;

@RestController
@RequestMapping("/api/telegram")
public class TelegramController {

    @Autowired
    private TelegramMessagingService service;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/message/broadcast", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendMessageToAllActiveUsers(@RequestBody JsonResponse jsonResponse,
            @RequestParam("uniqueNumbers") boolean uniqueNumbers, @RequestParam("activeUsers") boolean activeUsers) {
        log.info("Rest to sendMessageToAllActiveUsers");

        try {
            if (uniqueNumbers) {
                return new ResponseEntity(
                        new JsonResponse(
                                service.broadcastMessageToDistinctPhoneNumbers(jsonResponse.getMessage(), activeUsers)),
                        org.springframework.http.HttpStatus.OK);
            } else {
                return new ResponseEntity(
                        new JsonResponse(service.broadcastMessage(jsonResponse.getMessage(), activeUsers)),
                        org.springframework.http.HttpStatus.OK);
            }

        } catch (Exception e) {
            log.error("Exception occurred : {} ", e.getMessage(), e);
            return new ResponseEntity<JsonResponse>(new JsonResponse("Exception adding alert"),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/message/broadcast/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendMessageToSingleUser(@PathVariable int id, @RequestBody JsonResponse jsonResponse) {
        log.info("Rest to sendMessageToSingleUser");

        try {
            return new ResponseEntity(new JsonResponse(service.sendMessageToId(id, jsonResponse.getMessage())),
                    org.springframework.http.HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred : {} ", e.getMessage(), e);
            return new ResponseEntity<JsonResponse>(new JsonResponse("Exception adding alert"),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    
    @RequestMapping(value = "/message/userMessage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendMessageToMultipleUserWithChatId(@RequestBody JSONRequestMultiple jsonResponse) {
        log.info("Rest to sendMessageToMultipleUserWithChatId");
        String[] splitString = jsonResponse.getCommaSeperatedChatId().split(",");
        JSONResponseMultiple response = new JSONResponseMultiple();
        
        for(String chatId: splitString) {
            try {
                chatId = chatId.trim();

                service.sendMessageToChatId(String.valueOf(chatId), jsonResponse.getMessage());
                response.addtoSuccessList(chatId);
                
            } catch (Exception e) {
                log.error("Exception occurred : {} ", e.getMessage(), e);
                response.addtoErrorList(chatId, e.getMessage());
            }
        }
        return new ResponseEntity<JSONResponseMultiple>(response,org.springframework.http.HttpStatus.OK);
        
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/message/userMessage/{chatId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendMessageToSingleUserWithChatId(@PathVariable int chatId,
            @RequestBody JsonResponse jsonResponse) {
        log.info("Rest to sendMessageToSingleUser");

        try {
            return new ResponseEntity(service.sendMessageToChatId(String.valueOf(chatId), jsonResponse.getMessage()),
                    org.springframework.http.HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred : {} ", e.getMessage(), e);
            return new ResponseEntity<JsonResponse>(new JsonResponse("Exception adding alert"),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

}
/*
 * @ApiOperation(value = "Update registration detail", authorizations =
 * { @Authorization(value="basicAuth") })
 */