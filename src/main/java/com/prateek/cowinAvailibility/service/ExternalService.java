package com.prateek.cowinAvailibility.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;

import com.prateek.cowinAvailibility.configuration.AppConfiguration;
import com.prateek.cowinAvailibility.dto.cowinResponse.CowinResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ExternalService {

    public String checkByPincode;
    public String checkByDistrict;
    private SimpleDateFormat formatter;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AppConfiguration appConfiguration;

    @PostConstruct
    public void PostConstruct() {
        this.formatter = new SimpleDateFormat(this.appConfiguration.getDateFormat());
        this.checkByPincode = this.appConfiguration.getCheckByPincodeURL();
        this.checkByDistrict = this.appConfiguration.getCheckByDistrictURL();
    }

    public CowinResponse getData(int districtOrPincode, boolean isPinCode) {

        ResponseEntity<CowinResponse> response = getDataWithRetry(districtOrPincode, isPinCode);

        if (null != response && response.getStatusCode() == HttpStatus.OK) {
            log.info("Got Result from API   response code " + response.getStatusCode());
            return response.getBody();
        } else {
            log.info("Got null  from API response code " + response);
        }

        return null;
    }

    private ResponseEntity<CowinResponse> getDataWithRetry(int districtOrPincode, boolean isPinCode) {
        try {
            ResponseEntity<CowinResponse> response = getParsedData(districtOrPincode, isPinCode);
            log.info("Got Result from API   response code " + response.getStatusCode());
            return response;
        } catch (Exception e) {
            log.error("Exception occurred calling cowin api: " + e.getMessage());
        }
        return null;
    }

    private ResponseEntity<CowinResponse> getParsedData(int districtOrPincode, boolean isPinCode) {
        RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        headers.add("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<String>(
                "parameters", headers);
        headers.add("accept-encoding", "gzip, deflate, br");
        headers.add("accept-language", "");
        headers.add("", "en-US,en;q=0.9");

        String formattedDate = formatter.format(new Date());
        String url = null;
        if (isPinCode) {
            url = checkByPincode.replace("{pinCode}", String.valueOf(districtOrPincode));
        } else {
            url = checkByDistrict.replace("{districtId}", String.valueOf(districtOrPincode));
        }

        url = url.replace("{dateVal}", formattedDate);
        log.info("Initiaing API Call: " + url);
        ResponseEntity<CowinResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity,
                CowinResponse.class);

        return response;
    }

    private SimpleClientHttpRequestFactory getClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(2000);
        clientHttpRequestFactory.setReadTimeout(2000);
        return clientHttpRequestFactory;
    }

}
