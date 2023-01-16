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
        responseList.add("Thanks for using the chatbot.");
        responseList.add("Since the inception of the application enough, I was fortuntate enough to recieve thousands of customers and a wonderful feedback from you all. I am happy that this application was able to help (even though a little bit)"+
        "thousands of my fellow Indians in scheduling their covid 19 vaccinations. We all have suffered lose or may know someone who suffered. I wanted to change and minimize the impact using this application"
        +"I hope you and your family are safe now.");
        responseList.add(" \n\nSince the vaccine is now available directly, I have decomissioned the application now. ");
        responseList.add("I highly appreciate your confidence in the application and want to thnkyou again for using it.");
        responseList.add("Sincerly \nPrateek Mishra");
        responseList.add("Linkedin: https://www.linkedin.com/in/prateek-mishra-61aa4658/ ");
        responseList.add("Read Success story here: https://medium.com/p/9426aefe1a03");
        return responseList;
    }

}
