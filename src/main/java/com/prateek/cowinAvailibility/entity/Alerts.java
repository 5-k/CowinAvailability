package com.prateek.cowinAvailibility.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Spring Entity Class : auto generate using generate ddl as true, or run the
 * SQL Scripts in resource folder
 * 
 * @author prateek.mishra
 *
 */
@Entity
@Getter
@Setter
@Table(name = "alerts")
public class Alerts {

    public Alerts() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "state")
    private String state;

    @Column(name = "city")
    private String city;

    @Column(name = "pincode")
    private int pincode;

    @Column(name = "district_id")
    private int districtId;

    @Column(name = "phone")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "active")
    private boolean active;

    @Column(name = "age")
    private int age;

    @Column(name = "vaccine_type", nullable = false)
    private String vaccineType;

    @Column(name = "dosage_type")
    private int doseageType;

    @Column(name = "is_pincode_search", nullable = false)
    private boolean isPinCodeSearch;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "modified_at")
    private Date modifiedAt;

    @Column(name = "notification_type")
    private String notificationType; // 0 Whatsapp, 1 sms, 2 Email, 3 Telegram {Comma Seperated List Expected}

    @OneToMany(mappedBy = "alert", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Notifications> notifications;

    public void addNotifications(Notifications not) {
        if (null == notifications) {
            this.notifications = new ArrayList<>();
        }
        not.setAlert(this);
        this.notifications.add(not);
    }

    public List<Notifications> getNotifications() {
        if (null == notifications) {
            this.notifications = new ArrayList<>();
        }
        return notifications;
    }

    public void setNotifications(List<Notifications> notifications) {
        this.notifications = notifications;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getPincode() {
        return pincode;
    }

    public void setPincode(int pincode) {
        this.pincode = pincode;
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getVaccineType() {
        return vaccineType;
    }

    public void setVaccineType(String vaccineType) {
        this.vaccineType = vaccineType;
    }

    public boolean isPinCodeSearch() {
        return isPinCodeSearch;
    }

    public void setPinCodeSearch(boolean isPinCodeSearch) {
        this.isPinCodeSearch = isPinCodeSearch;
    }

    public Alerts(String name, String state, String city, int pincode, int districtId, String phoneNumber, String email,
            boolean active, int age, String vaccineType, int doseageType, boolean isPinCodeSearch,
            String notificationType) {
        this.name = name;
        this.state = state;
        this.city = city;
        this.pincode = pincode;
        this.districtId = districtId;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.active = active;
        this.age = age;
        this.vaccineType = vaccineType;
        this.doseageType = doseageType;
        this.isPinCodeSearch = isPinCodeSearch;
        this.notificationType = notificationType;
    }

    public int getDoseageType() {
        return doseageType;
    }

    public void setDoseageType(int doseageType) {
        this.doseageType = doseageType;
    }

    @Override
    public String toString() {
        return "Alerts [active=" + active + ", age=" + age + ", city=" + city + ", createdAt=" + createdAt
                + ", districtId=" + districtId + ", doseageType=" + doseageType + ", email=" + email + ", id=" + id
                + ", isPinCodeSearch=" + isPinCodeSearch + ", modifiedAt=" + modifiedAt + ", name=" + name
                + ", notificationType=" + notificationType + ", notifications=" + notifications + ", phoneNumber="
                + phoneNumber + ", pincode=" + pincode + ", state=" + state + ", vaccineType=" + vaccineType + "]";
    }

    public Alerts(int id, String name, String state, String city, int pincode, int districtId, String phoneNumber,
            String email, boolean active, int age, String vaccineType, int doseageType, boolean isPinCodeSearch,
            Date createdAt, Date modifiedAt, String notificationType, List<Notifications> notifications) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.city = city;
        this.pincode = pincode;
        this.districtId = districtId;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.active = active;
        this.age = age;
        this.vaccineType = vaccineType;
        this.doseageType = doseageType;
        this.isPinCodeSearch = isPinCodeSearch;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.notificationType = notificationType;
        this.notifications = notifications;
    }

}