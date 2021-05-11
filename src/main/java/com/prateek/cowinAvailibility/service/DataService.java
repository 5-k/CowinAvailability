package com.prateek.cowinAvailibility.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Component;

@Component
public class DataService {

    private Map<String, String> actionResponseJson;
    private Map<String, Map<String, Integer>> cityMap;
    private List<Map<String, Integer>> stateMap;

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void loadResource() {
        try {

            ObjectMapper mapper = new ObjectMapper();

            InputStream dataStream = getClass().getClassLoader().getResourceAsStream("data.json");
            InputStream stateStream = getClass().getClassLoader().getResourceAsStream("stateList.json");
            InputStream cityStream = getClass().getClassLoader().getResourceAsStream("cityList.json");

            this.actionResponseJson = mapper.readValue(dataStream, Map.class);
            this.stateMap = (List<Map<String, Integer>>) mapper.readValue(stateStream, Map.class).get("states");
            this.cityMap = (Map<String, Map<String, Integer>>) mapper.readValue(cityStream, Map.class).get("districts");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> getActionResponseJson() {
        return actionResponseJson;
    }

    public Map<String, Map<String, Integer>> getCityMap() {
        return cityMap;
    }

    public List<Map<String, Integer>> getStateMap() {
        return stateMap;
    }

}
