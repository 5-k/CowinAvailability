package com.prateek.cowinAvailibility.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.prateek.cowinAvailibility.utility.Constants;
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

    public Feedback(String phoneNumber, String feedback) {
        this.phoneNumber = phoneNumber;
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
