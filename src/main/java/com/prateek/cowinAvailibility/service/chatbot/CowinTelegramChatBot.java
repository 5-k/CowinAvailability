package com.prateek.cowinAvailibility.service.chatbot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.prateek.cowinAvailibility.entity.Alerts;

@Component
public class CowinTelegramChatBot {

    
    Map<Long, Alerts> alertMap;
    Map<Long, String> previousQuestion;

    Map<String, Map<String, Integer>> cityMap;
    List<Map<String, Integer>> stateMap;

    @PostConstruct
    public void loadResource() {
        

    }

    public List<String> getResponseForMessage(String messageText, long chatId) {
        messageText = messageText.trim().toLowerCase();

        List<String> responseList = new ArrayList<>();
        responseList.add("Thankyou for using the Covid 19 vaccine alert generator and slot schedular telegram chatbot.\n\nSince the inception of the application, I was fortunate enough to receive requests from tens of thousands of customers. Apart from the alerts, the most heart felt moment for me was when I received a lot of wonderful feedbacks from you all.\n\nI am happy that this application was able to help (even though a little bit) thousands of my fellow Indians in scheduling their covid 19 vaccinations.\n\nWe all have suffered loss or may know someone who suffered. I wanted to help people get vaccines so that together we fight this virus together.\n\nWith the help of this application I was able to help people and received their blessings. :)\n\nSituation has improved and changed now, the vaccines are now available directly. Therefore, I have decided to decommission the application now. Should there be any help required, I am always open to suggestions and willing to help in the best way possible.\nI highly appreciate your confidence in the application and want to Thank you again for using the application\n\nSincerely \nPrateek Mishra\n\nLinkedIn: https://www.linkedin.com/in/prateek-mishra-61aa4658/\n\nTelegram: https://t.me/imprateekm \n\nRead Success story & provide feedbacks here: https://medium.com/p/9426aefe1a03");
        return responseList;
    }

}
