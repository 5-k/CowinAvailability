package com.prateek.cowinAvailibility.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "chat_history")
public class TelegramChatHistory {

    public TelegramChatHistory() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "chatId")
    private long chatId;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_input_message")
    private boolean isInputMessage;

    @Column(name = "created_at")
    private Date createdAt;

    public boolean isInputMessage() {
        return isInputMessage;
    }

    public void setInputMessage(boolean isInputMessage) {
        this.isInputMessage = isInputMessage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public TelegramChatHistory(long chatId, String message, boolean isInputMessage, Date createdAt) {
        this.chatId = chatId;
        this.message = message;
        this.isInputMessage = isInputMessage;
        this.createdAt = createdAt;
    }

}
