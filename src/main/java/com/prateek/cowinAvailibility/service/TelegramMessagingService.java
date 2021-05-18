package com.prateek.cowinAvailibility.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.prateek.cowinAvailibility.entity.Alerts;
import com.prateek.cowinAvailibility.repo.AlertRepo;
import com.prateek.cowinAvailibility.service.chatbot.ITelegramSlotPoller;
import com.prateek.cowinAvailibility.service.chatbot.TelegramSlotPoller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class TelegramMessagingService {

    @Autowired
    @Qualifier("telegramSlotPoller")
    private ITelegramSlotPoller service;

    @Autowired
    private AlertRepo alertRepo;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public String broadcastMessage(String message, boolean activeUsers) {
        List<Alerts> alerts;
        if (activeUsers) {
            alerts = alertRepo.findByActiveTrue();
        } else {
            alerts = alertRepo.findByActiveFalse();
        }
        for (int i = 0; i < alerts.size(); i++) {
            Alerts alt = alerts.get(i);
            try {
                if (null != alt.getPhoneNumber() && alt.getPhoneNumber().contains("telegram:")) {
                    log.info("Sending broadcast message to chat");
                    sendNotification(alt, message);
                }
            } catch (Exception e) {
                log.error("Failed to send broadcast for alert " + alt.toString(), e);
            }

        }
        return "Success";
    }

    public String broadcastMessageToDistinctPhoneNumbers(String message, boolean activeUsers) {
        List<Alerts> alerts;
        if (activeUsers) {
            alerts = alertRepo.findByActiveTrue();
        } else {
            alerts = alertRepo.findByActiveFalse();
        }
        Set<String> setOfPhone = new HashSet<String>();

        for (int i = 0; i < alerts.size(); i++) {
            Alerts alt = alerts.get(i);
            try {
                if (null != alt.getPhoneNumber() && alt.getPhoneNumber().contains("telegram:")) {
                    String chatId = getChatIdByPhoneNumber(alt.getPhoneNumber());
                    log.info("Sending broadcast message to chat " + chatId);
                    if (setOfPhone.contains(chatId)) {
                        log.warn("Not Sending message since already sent: " + chatId);
                    } else {
                        sendNotification(alt, message);
                        setOfPhone.add(chatId);
                    }
                } else {
                    log.warn("Not Sending message since not a telegram chatid");
                }
            } catch (Exception e) {
                log.error("Failed to send broadcast for alert " + alt.toString(), e);
            }

        }
        return "Success";
    }

    public String sendMessageToId(int id, String message) {
        Optional<Alerts> s = alertRepo.findById(id);
        if (null != s && s.isPresent()) {
            Alerts alt = s.get();
            String chatId = getChatIdByPhoneNumber(alt.getPhoneNumber());
            service.sendResponse(chatId, message, true, false);
        }
        return "Success";
    }

    public String sendMessageToChatId(String chatId, String message) {
        service.sendResponse(chatId, message, true, false);
        return "Success";
    }

    @Async
    private void sendNotification(Alerts alt, String message) {
        String chatId = getChatIdByPhoneNumber(alt.getPhoneNumber());
        log.info("Sending broadcast message to chat id " + chatId);
        service.sendResponse(chatId, message, true, false);
    }

    private String getChatIdByPhoneNumber(String phoneNumber) {
        return phoneNumber.substring("telegram:".length());
    }
}
