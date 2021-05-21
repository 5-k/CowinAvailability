package com.prateek.cowinAvailibility.service.chatbot;

import java.util.Set;

import com.prateek.cowinAvailibility.dto.cowinResponse.AvlResponse;
import com.prateek.cowinAvailibility.entity.Alerts;

public interface ITelegramSlotPoller {

    public String sendVaccineUpdates(Alerts alert, String message);

    public String sendVaccineUpdates(Alerts alert, Set<AvlResponse> avlResponseList);

    public String sendVaccineUpdatestoSelf(Alerts alert, Set<AvlResponse> avlResponseList);

    public String sendVaccineUpdatestoSelf(String message);

    public String sendResponse(String chatId, String response, boolean enableMarkdown, boolean enableHtml);
}
