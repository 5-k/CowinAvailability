package com.prateek.cowinAvailibility.entity;

import java.util.Comparator;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "notifications")
public class Notifications implements Comparable<Notifications>, Comparator<Notifications> {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "notification_cost")
    private String notificationCost;

    @Column(name = "notification_type")
    private int notificationType;

    @ManyToOne
    @JoinColumn(name = "alert_id")
    private Alerts alert;

    public Alerts getAlert() {
        return alert;
    }

    public void setAlert(Alerts alert) {
        this.alert = alert;
    }

    public String getNotificationCost() {
        return notificationCost;
    }

    public void setNotificationCost(String notificationCost) {
        this.notificationCost = notificationCost;
    }

    public Notifications() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }

    public Notifications(int id, Date createdAt, String phoneNumber, Alerts alert, String notificationCost,
            int notificationType) {
        this.id = id;
        this.createdAt = createdAt;
        this.phoneNumber = phoneNumber;
        this.notificationCost = notificationCost;
        this.notificationType = notificationType;
        this.alert = alert;
    }

    public Notifications(Date createdAt, String phoneNumber, Alerts alert, String notificationCost,
            int notificationType) {
        this.createdAt = createdAt;
        this.phoneNumber = phoneNumber;
        this.notificationCost = notificationCost;
        this.notificationType = notificationType;
        this.alert = alert;
    }

    @Override
    public int compareTo(Notifications o) {
        return o.getCreatedAt().compareTo(getCreatedAt());
    }

    @Override
    public int compare(Notifications o1, Notifications o2) {
        return o2.getCreatedAt().compareTo(o1.getCreatedAt());
    }

}
