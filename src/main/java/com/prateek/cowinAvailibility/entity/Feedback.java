package com.prateek.cowinAvailibility.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.prateek.cowinAvailibility.utility.Utils;

@Entity
@Table(name = "feedback")
public class Feedback {

    public Feedback() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "created_at")
    private Date createdAt;

    public Feedback(String phoneNumber, String feedback, Date createdAt) {
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
        this.feedback = Utils.getEmotionLessString(feedback);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = Utils.getEmotionLessString(feedback);
    }

}
