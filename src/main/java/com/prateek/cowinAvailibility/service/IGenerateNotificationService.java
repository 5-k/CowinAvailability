package com.prateek.cowinAvailibility.service;

import java.util.Set;

import com.prateek.cowinAvailibility.dto.cowinResponse.AvlResponse;
import com.prateek.cowinAvailibility.entity.Alerts;

public interface IGenerateNotificationService {
    public void notifyUsers(Alerts alert, Set<AvlResponse> avlResponseList);
}