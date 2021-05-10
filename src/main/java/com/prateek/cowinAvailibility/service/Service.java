package com.prateek.cowinAvailibility.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.prateek.cowinAvailibility.dto.AlertDTO;
import com.prateek.cowinAvailibility.entity.Alerts;
import com.prateek.cowinAvailibility.repo.AlertRepo;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author prateek.mishra Service class performaing actual CRUD operations
 */
@org.springframework.stereotype.Service
public class Service {

    @Autowired
    private AlertRepo alertRepo;

    // public Alerts(String name, String state, String city, int pincode, int
    // districtId, String phoneNumber,
    // boolean active, int age, String vaccineType)

    public int addAlert(AlertDTO aDto) {

        if (aDto.getPincode() > 0) {
            aDto.setPinCodeSearch(true);
        }

        aDto.setActive(true);
        Alerts alert = new Alerts(aDto.getName(), aDto.getState(), aDto.getCity(), aDto.getPincode(),
                aDto.getDistrictId(), aDto.getPhoneNumber(), aDto.isActive(), aDto.getAge(), aDto.getVaccineType(),
                aDto.isPinCodeSearch(), aDto.getNotificationType(), aDto.getEmailAddress());

        return alertRepo.save(alert).getId();
    }

    public int removeAlertById(int id) {

        Optional<Alerts> alerts = alertRepo.findById(id);
        if (null == alerts) {
            return 0;
        }
        Alerts alert = alerts.get();
        alert.setActive(false);
        alertRepo.save(alert);
        return 1;
    }

    public int removeAlertByPhone(String phone) {

        List<Alerts> alerts = alertRepo.findByPhoneNumber(phone);
        if (null == alerts || alerts.size() == 0) {
            return 0;
        }

        List<Alerts> updatedAlerts = new ArrayList<>();

        for (int i = 0; i < alerts.size(); i++) {
            Alerts alt = alerts.get(i);
            alt.setActive(false);
            updatedAlerts.add(alt);
        }

        alertRepo.saveAll(updatedAlerts);
        return 1;
    }

    public List<AlertDTO> getAlerts() {
        List<Alerts> alerts = alertRepo.findAll();
        return convertToDtos(alerts);
    }

    public AlertDTO getAlertsById(int id) {
        Alerts alert = alertRepo.findById(id).get();
        return convertToDto(alert);
    }

    public List<AlertDTO> getAlertByMobileNumber(AlertDTO aDto) {
        List<Alerts> alerts = alertRepo.findByPhoneNumber(aDto.getPhoneNumber());
        return convertToDtos(alerts);
    }

    private List<AlertDTO> convertToDtos(List<Alerts> alerts) {
        List<AlertDTO> dtos = new ArrayList<>();
        for (int i = 0; i < alerts.size(); i++) {

            AlertDTO alert = convertToDto(alerts.get(i));
            dtos.add(alert);
        }
        return dtos;
    }

    private AlertDTO convertToDto(Alerts alert) {

        AlertDTO alertVal = new AlertDTO(alert.getId(), alert.getName(), alert.getState(), alert.getCity(),
                alert.getPincode(), alert.getDistrictId(), alert.getPhoneNumber(), alert.isActive(), alert.getAge(),
                alert.getVaccineType(), alert.isPinCodeSearch(), alert.getNotificationType(), alert.getEmail());

        return alertVal;
    }
}
