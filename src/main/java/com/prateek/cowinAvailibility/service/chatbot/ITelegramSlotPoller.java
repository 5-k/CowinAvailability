package com.prateek.cowinAvailibility.service.chatbot;

import java.util.Set;

import com.prateek.cowinAvailibility.dto.cowinResponse.AvlResponse;
import com.prateek.cowinAvailibility.entity.Alerts;

public interface ITelegramSlotPoller {

    public void sendVaccineUpdates(Alerts alert, String message);

    public void sendVaccineUpdates(Alerts alert, Set<AvlResponse> avlResponseList);

    public void sendVaccineUpdatestoSelf(String message);

}
