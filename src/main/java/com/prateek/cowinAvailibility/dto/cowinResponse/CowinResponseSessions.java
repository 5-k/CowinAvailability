package com.prateek.cowinAvailibility.dto.cowinResponse;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CowinResponseSessions implements Serializable {
    private String session_id;
    private String date;
    private int available_capacity;
    private int min_age_limit;
    private String vaccine;
    private List<String> slots;
    private int available_capacity_dose1;
    private int available_capacity_dose2;

    @Override
    public String toString() {
        return "CowinResponseSessions [available_capacity=" + available_capacity + ", available_capacity_dose1="
                + available_capacity_dose1 + ", available_capacity_dose2=" + available_capacity_dose2 + ", date=" + date
                + ", min_age_limit=" + min_age_limit + ", session_id=" + session_id + ", slots=" + slots + ", vaccine="
                + vaccine + "]";
    }

    public String getSession_id() {
        return session_id;
    }

    public int getAvailable_capacity_dose1() {
        return available_capacity_dose1;
    }

    public void setAvailable_capacity_dose1(int available_capacity_dose1) {
        this.available_capacity_dose1 = available_capacity_dose1;
    }

    public int getAvailable_capacity_dose2() {
        return available_capacity_dose2;
    }

    public void setAvailable_capacity_dose2(int available_capacity_dose2) {
        this.available_capacity_dose2 = available_capacity_dose2;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAvailable_capacity() {
        return available_capacity;
    }

    public void setAvailable_capacity(int available_capacity) {
        this.available_capacity = available_capacity;
    }

    public int getMin_age_limit() {
        return min_age_limit;
    }

    public void setMin_age_limit(int min_age_limit) {
        this.min_age_limit = min_age_limit;
    }

    public String getVaccine() {
        return vaccine;
    }

    public void setVaccine(String vaccine) {
        this.vaccine = vaccine;
    }

    public List<String> getSlots() {
        return slots;
    }

    public void setSlots(List<String> slots) {
        this.slots = slots;
    }

}
