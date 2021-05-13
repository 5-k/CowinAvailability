package com.prateek.cowinAvailibility.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "app")
public class AppConfiguration {

    private String telegramBotName;

    private String telegramBotToken;

    private String emailFrom;

    private String emailHost;

    private String emailSocketFactory;

    private String emailSocketFactoryClass;

    private String emailAuth;

    private String emailSmtpPort;

    private String emailFromPassword;

    private String checkByPincodeURL;

    private String checkByDistrictURL;

    private String dateFormat;

    private int maxNotificationPerAlertPerDay;

    private int timeDifferenceBetweenPreviousNotificationInMins;

    private String appHost;

    private String host;

    private int appPort;

    private String twiloAccoundSid;

    private String twiloAuthToken;

    private String twiloSendNumber;

    private String checkAVLCronJob;

    private String logChatbotCron;

    private String debugTelegramChatId;

    public String getTwiloAccoundSid() {
        return twiloAccoundSid;
    }

    public void setTwiloAccoundSid(String twiloAccoundSid) {
        this.twiloAccoundSid = twiloAccoundSid;
    }

    public String getTwiloAuthToken() {
        return twiloAuthToken;
    }

    public void setTwiloAuthToken(String twiloAuthToken) {
        this.twiloAuthToken = twiloAuthToken;
    }

    public String getTwiloSendNumber() {
        return twiloSendNumber;
    }

    public void setTwiloSendNumber(String twiloSendNumber) {
        this.twiloSendNumber = twiloSendNumber;
    }

    public String getAppHostNameURL() {
        return appHost;
    }

    public void setAppHostNameURL(String appHostNameURL) {
        this.appHost = appHostNameURL;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getAppPort() {
        return appPort;
    }

    public void setAppPort(int appPort) {
        this.appPort = appPort;
    }

    public String getEmailFromPassword() {
        return emailFromPassword;
    }

    public void setEmailFromPassword(String emailFromPassword) {
        this.emailFromPassword = emailFromPassword;
    }

    public String getTelegramBotName() {
        return telegramBotName;
    }

    public void setTelegramBotName(String telegramBotName) {
        this.telegramBotName = telegramBotName;
    }

    public String getTelegramBotToken() {
        return telegramBotToken;
    }

    public void setTelegramBotToken(String telegramBotToken) {
        this.telegramBotToken = telegramBotToken;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }

    public String getEmailHost() {
        return emailHost;
    }

    public void setEmailHost(String emailHost) {
        this.emailHost = emailHost;
    }

    public String getEmailSocketFactory() {
        return emailSocketFactory;
    }

    public void setEmailSocketFactory(String emailSocketFactory) {
        this.emailSocketFactory = emailSocketFactory;
    }

    public String getEmailSocketFactoryClass() {
        return emailSocketFactoryClass;
    }

    public void setEmailSocketFactoryClass(String emailSocketFactoryClass) {
        this.emailSocketFactoryClass = emailSocketFactoryClass;
    }

    public String getEmailAuth() {
        return emailAuth;
    }

    public void setEmailAuth(String emailAuth) {
        this.emailAuth = emailAuth;
    }

    public String getEmailSmtpPort() {
        return emailSmtpPort;
    }

    public void setEmailSmtpPort(String emailSmtpPort) {
        this.emailSmtpPort = emailSmtpPort;
    }

    public String getCheckByPincodeURL() {
        return checkByPincodeURL;
    }

    public void setCheckByPincodeURL(String checkByPincodeURL) {
        this.checkByPincodeURL = checkByPincodeURL;
    }

    public String getCheckByDistrictURL() {
        return checkByDistrictURL;
    }

    public void setCheckByDistrictURL(String checkByDistrictURL) {
        this.checkByDistrictURL = checkByDistrictURL;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public int getMaxNotificationPerAlertPerDay() {
        return maxNotificationPerAlertPerDay;
    }

    public void setMaxNotificationPerAlertPerDay(int maxNotificationPerAlertPerDay) {
        this.maxNotificationPerAlertPerDay = maxNotificationPerAlertPerDay;
    }

    public int getTimeDifferenceBetweenPreviousNotificationInMins() {
        return timeDifferenceBetweenPreviousNotificationInMins;
    }

    public void setTimeDifferenceBetweenPreviousNotificationInMins(
            int timeDifferenceBetweenPreviousNotificationInMins) {
        this.timeDifferenceBetweenPreviousNotificationInMins = timeDifferenceBetweenPreviousNotificationInMins;
    }

    public String getCheckAVLCronJob() {
        return checkAVLCronJob;
    }

    public void setCheckAVLCronJob(String checkAVLCronJob) {
        this.checkAVLCronJob = checkAVLCronJob;
    }

    public String getLogChatbotCron() {
        return logChatbotCron;
    }

    public void setLogChatbotCron(String logChatbotCron) {
        this.logChatbotCron = logChatbotCron;
    }

    public String getDebugTelegramChatId() {
        return debugTelegramChatId;
    }

    public void setDebugTelegramChatId(String debugTelegramChatId) {
        this.debugTelegramChatId = debugTelegramChatId;
    }

}
