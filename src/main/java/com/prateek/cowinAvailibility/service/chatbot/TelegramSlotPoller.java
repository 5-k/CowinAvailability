package com.prateek.cowinAvailibility.service.chatbot;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.prateek.cowinAvailibility.configuration.AppConfiguration;
import com.prateek.cowinAvailibility.dto.cowinResponse.AvlResponse;
import com.prateek.cowinAvailibility.entity.Alerts;
import com.prateek.cowinAvailibility.utility.Utils;

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
        
        String messageText = update.getMessage().getText();
        long longchatId = update.getMessage().getChatId();
        String chatId = String.valueOf(longchatId);
        List<String> response = cowinTelegramChatBot.getResponseForMessage(messageText, longchatId);
        for (int i = 0; i < response.size(); i++) {
            sendResponse(chatId, response.get(i), true,  true);
            chatLogger.logChat(longchatId, response.get(i), false);
        }
    }

    @Override
    public String sendResponse(String chatId, String response, boolean enableMarkdown, boolean enableHtml) {
        if (null == response || response.length() == 0) {
            log.warn("Null message, not sending message: " + chatId);
            return null;
        }

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
            }
        }
        return responseList.size() + "___" + response.length();
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

    @Override
    public String sendVaccineUpdates(Alerts alert, String message) {
        log.debug("Response to publish ", message);
        String chatId = alert.getPhoneNumber().substring(alert.getPhoneNumber().indexOf(":") + 1);
        return sendResponse(chatId, message, true, false);
    }

    @Override
    public String sendVaccineUpdates(Alerts alert, Set<AvlResponse> avlResponseList) {
        String message = Utils.getTelegramAlertMessage(alert, avlResponseList, false);
        String chatId = alert.getPhoneNumber().substring(alert.getPhoneNumber().indexOf(":") + 1);
        sendVaccineUpdates(chatId, message);
        return String.valueOf(avlResponseList.size());
    }

    @Override
    public String sendVaccineUpdatestoSelf(Alerts alert, Set<AvlResponse> avlResponseList) {
        String message = Utils.getTelegramAlertMessage(alert, avlResponseList, false);
        String chatId = appConfiguration.getDebugTelegramChatId();
        log.trace("Sending message " + message + "\n to chat id " + chatId);
        return sendVaccineUpdates(chatId, message);
    }

    @Async
    @Override
    public String sendVaccineUpdatestoSelf(String message) {
        String chatId = appConfiguration.getDebugTelegramChatId();
        return sendResponse(chatId, message, true, false);
    }

    private String sendVaccineUpdates(String chatId, String message) {
        return sendResponse(chatId, message, true, false);
    }

}