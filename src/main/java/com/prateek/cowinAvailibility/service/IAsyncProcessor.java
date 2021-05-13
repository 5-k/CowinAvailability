package com.prateek.cowinAvailibility.service;

import java.util.Set;

import com.prateek.cowinAvailibility.dto.cowinResponse.AvlResponse;
import com.prateek.cowinAvailibility.dto.cowinResponse.CowinResponse;
import com.prateek.cowinAvailibility.entity.Alerts;

public interface IAsyncProcessor {

    void processAndNotify(Alerts alert);

    CowinResponse getResponseForAlert(Alerts alert);

    Set<AvlResponse> processResponse(Alerts alert, CowinResponse response);

    CowinResponse checkByPinCode(int pincode);

    CowinResponse checkAvlByDistrict(int districtId);
}