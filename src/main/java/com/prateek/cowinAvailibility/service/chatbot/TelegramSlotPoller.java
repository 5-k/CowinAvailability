package com.prateek.cowinAvailibility.service.chatbot;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.prateek.cowinAvailibility.configuration.AppConfiguration;
import com.prateek.cowinAvailibility.dto.cowinResponse.AvlResponse;
import com.prateek.cowinAvailibility.dto.cowinResponse.CowinResponseSessions;
import com.prateek.cowinAvailibility.entity.Alerts;
import com.prateek.cowinAvailibility.utility.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.*;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component("telegramSlotPoller")
public class TelegramSlotPoller extends TelegramLongPollingBot implements ITelegramSlotPoller {

    @Autowired
    @Qualifier("ChatBotLogger")
    private IChatBotLogger chatLogger;

    @Autowired
    private AppConfiguration appConfiguration;

    @Autowired
    private CowinTelegramChatBot cowinTelegramChatBot;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public TelegramSlotPoller() {
    }

    @Override
    public String getBotUsername() {
        return appConfiguration.getTelegramBotName();
    }

    @Override
    public String getBotToken() {
        return appConfiguration.getTelegramBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long longchatId = update.getMessage().getChatId();

            String messageText = update.getMessage().getText();
            chatLogger.logChat(longchatId, messageText, true);

            log.info("Input Message", messageText);
            log.info("updateId - " + update.getUpdateId() + " - getChatId " + update.getMessage().getChatId()
                    + " isCommand- " + update.getMessage().isCommand() + " - getMessageId "
                    + update.getMessage().getMessageId());

            List<String> response = cowinTelegramChatBot.getResponseForMessage(messageText, longchatId);
            log.debug("Got Message for the input - " + messageText);

            String chatId = String.valueOf(longchatId);
            log.debug("response --> " + response);
            for (int i = 0; i < response.size(); i++) {
                sendResponse(chatId, response.get(i), true, false);
                chatLogger.logChat(longchatId, response.get(i), false);
            }
        } else {
            sendResponse(update.getMessage().getChatId().toString(), "Please send text message", true, false);
        }
    }

    public void sendResponse(String chatId, String response, boolean enableMarkdown, boolean enableHtml) {
        List<String> responseList = Utils.splitToNChar(response, 4000);

        for (String resp : responseList) {
            if (null == resp || resp.trim().length() == 0) {
                continue;
            }

            SendMessage message = new SendMessage(chatId, resp);
            message.enableMarkdown(enableMarkdown);
            if (enableHtml) {
                message.enableHtml(enableHtml);
            }

            try {
                execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                log.error("Exception for sending message to chat id " + chatId + " with exception " + e.getMessage(),
                        e);
                e.printStackTrace();
            }
        }

    }

    // Not Used
    public void registerTelegram() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
        } catch (org.telegram.telegrambots.meta.exceptions.TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendVaccineUpdates(Alerts alert, String message) {
        try {
            log.debug("Response to publish ", message);
            String chatId = alert.getPhoneNumber().substring(alert.getPhoneNumber().indexOf(":") + 1);
            sendVaccineUpdatestoSelf(message);
            sendResponse(chatId, message, true, false);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Async
    public void sendVaccineUpdates(Alerts alert, Set<AvlResponse> avlResponseList) {
        String message = Utils.getTelegramAlertMessage(alert, avlResponseList);
        String chatId = alert.getPhoneNumber().substring(alert.getPhoneNumber().indexOf(":") + 1);
        sendVaccineUpdates(chatId, message);
        sendVaccineUpdatestoSelf(message);
    }

    @Override
    public void sendVaccineUpdatestoSelf(Alerts alert, Set<AvlResponse> avlResponseList) {
        String message = Utils.getTelegramAlertMessage(alert, avlResponseList);
        String chatId = appConfiguration.getDebugTelegramChatId();
        log.debug("Sending message " + message + "\n to chat id " + chatId);
        sendVaccineUpdates(chatId, message);
    }

    private void sendVaccineUpdates(String chatId, String message) {
        try {
            sendResponse(chatId, message, true, false);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Async
    public void sendVaccineUpdatestoSelf(String message) {
        String chatId = appConfiguration.getDebugTelegramChatId();
        try {
            sendResponse(chatId, message, true, false);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}