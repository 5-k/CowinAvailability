package com.prateek.cowinAvailibility.controller;

import java.util.List;
import java.util.Optional;

import com.prateek.cowinAvailibility.entity.Alerts;
import com.prateek.cowinAvailibility.repo.AlertRepo;
import com.prateek.cowinAvailibility.service.TelegramMessagingService;
import com.prateek.cowinAvailibility.service.chatbot.TelegramSlotPoller;
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

@org.springframework.web.bind.annotation.RestController
public class TelegramController {

    @Autowired
    private TelegramMessagingService service;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/app/telegram/message/broadcast", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendMessageToAllActiveUsers(@RequestBody JsonResponse jsonResponse) {
        log.info("Rest to sendMessageToAllActiveUsers");

        try {
            return new ResponseEntity(new JsonResponse(service.broadcastMessage(jsonResponse.getMessage())),
                    org.springframework.http.HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred : {} ", e.getMessage(), e);
            return new ResponseEntity<JsonResponse>(new JsonResponse("Exception adding alert"),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/app/telegram/message/broadcast/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
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

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/app/telegram/message/userMessage/{chatId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendMessageToSingleUserWithChatId(@PathVariable int chatId,
            @RequestBody JsonResponse jsonResponse) {
        log.info("Rest to sendMessageToSingleUser");

        try {
            return new ResponseEntity(
                    new JsonResponse(service.sendMessageToChatId(String.valueOf(chatId)), jsonResponse.getMessage()),
                    org.springframework.http.HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred : {} ", e.getMessage(), e);
            return new ResponseEntity<JsonResponse>(new JsonResponse("Exception adding alert"),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

}
