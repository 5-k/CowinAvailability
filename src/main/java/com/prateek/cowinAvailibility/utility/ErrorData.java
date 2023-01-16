package com.prateek.cowinAvailibility.utility;

public class ErrorData {

    private String chatId;
    private String message;
    public ErrorData(){

    }
    public String getChatId() {
        return chatId;
    }
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public ErrorData(String chatId, String message) {
        this.chatId = chatId;
        this.message = message;
    }

    
}
