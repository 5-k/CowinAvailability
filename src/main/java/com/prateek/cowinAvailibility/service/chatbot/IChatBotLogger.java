package com.prateek.cowinAvailibility.service.chatbot;

public interface IChatBotLogger {

    void logChat(long chatId, String message, boolean isInput);

    void saveLogAsync();
}
