package com.prateek.cowinAvailibility.dto.cowinResponse;

import java.io.Serializable;

public class CowinVaccineFees implements Serializable {

    private String vaccine;
    private String fees;

    public String getVaccine() {
        return vaccine;
    }

    public void setVaccine(String vaccine) {
        this.vaccine = vaccine;
    }

    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public CowinVaccineFees(String vaccine, String fees) {
        this.vaccine = vaccine;
        this.fees = fees;
    }

    public CowinVaccineFees() {
    }
}
