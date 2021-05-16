package com.prateek.cowinAvailibility.service.chatbot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PreDestroy;

import com.prateek.cowinAvailibility.entity.TelegramChatHistory;
import com.prateek.cowinAvailibility.repo.TelegramChatHistoryRepo;
import com.prateek.cowinAvailibility.utility.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component("ChatBotLogger")
public class ChatBotLogger implements IChatBotLogger {

    @Autowired
    private TelegramChatHistoryRepo telegramChatHistoryRepo;

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private List<TelegramChatHistory> list;

    public ChatBotLogger() {
        list = new ArrayList<>();
        ;
    }

    @Override
    @Async
    public void logChat(long chatId, String message, boolean isInput) {
        try {
            if (null == message) {
                return;
            }
            list.add(new TelegramChatHistory(chatId, Utils.getEmotionLessString(message), isInput, new Date()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    @Scheduled(cron = "${app.logChatbotCron}")
    public void saveLogAsync() {
        try {
            List<TelegramChatHistory> tempList = new ArrayList<>(this.list);
            this.list = new ArrayList<>();
            this.telegramChatHistoryRepo.saveAll(tempList);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }

    }

    @PreDestroy
    public void predestroy() {
        saveLogAsync();
    }
}
