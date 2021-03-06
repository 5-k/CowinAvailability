package com.prateek.cowinAvailibility.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.prateek.cowinAvailibility.configuration.AppConfiguration;
import com.prateek.cowinAvailibility.dto.cowinResponse.AvlResponse;
import com.prateek.cowinAvailibility.dto.cowinResponse.CowinResponseSessions;
import com.prateek.cowinAvailibility.entity.Alerts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailServce {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final String mainMessage = "<div id=\":1cv\" class=\"Am Al editable LW-avf tS-tW tS-tY\" hidefocus=\"true\" aria-label=\"Message Body\" g_editable=\"true\" role=\"textbox\" aria-multiline=\"true\" contenteditable=\"true\" tabindex=\"1\" style=\"direction: ltr; min-height: 173px;\" itacorner=\"6,7:1,1,0,0\" spellcheck=\"true\">Hi There,<div><br></div><div>This is in reference with the alert setup by you mentioned below:</div><div><br></div>"
            + "<div><b>Alert </b>-&nbsp;${Alert}</div>------------------------------------<div><br></div>"
            + "<br></div><div><div></div>" + "${AllVaccinationDetails}" + "<div><br></div>" + "</div><div><br></div>"
            + "<div>To disable all alerts ,&nbsp;<a href=\"${disableAlertURL}\">Click Here</a></div><div><br></div>"
            + "<div>Get Vaccinated soon&nbsp;<img src=\"//ssl.gstatic.com/mail/emoji/v7/png48/emoji_u1f60e.png\" alt=\"\" goomoji=\"1f60e\" data-goomoji=\"1f60e\" style=\"margin: 0px 0.2ex; vertical-align: middle; height: 24px; width: 24px;\"></div>"
            + "<div><br>Regards</div>"
            + "<div><a href=\"https://www.linkedin.com/in/prateek-mishra-61aa4658/\">Connect on Linkedin</a></div>"
            + "</div>";

    private final String vaccinationInfoMessage = "</div>"
            + "<div style=\"background-color: \"rgb(243, 243, 243)\"; font-weight: \"bold\" font-style: \"italic\"; color: \"#c27ba0\" \">Vaccination Center : ${VaccinationCenter}"
            + "<div style=\"font-style: \"italics\"; font-weight:'bold' \" >Vaccine :${VaccineName}</div>"
            + "<div>Available Vaccine :${SlotAndCount}</div>";

    @Autowired
    private DataService dataService;

    private Map<String, String> actionResponseJson;

    @PostConstruct
    private void PostConstruct() {
        this.actionResponseJson = dataService.getActionResponseJson();
    }

    @Autowired
    private AppConfiguration appConfiguration;

    public void sendWelcomeMessage(Alerts alert) {
        String disableAlerts = appConfiguration.getAppHostNameURL() + "/api/Alerts/delete/" + alert.getId();
        String message = this.actionResponseJson.get("welcome").replace("${clickHere}", disableAlerts);
        String subject = "Welcome to Cowin Alerts";
        sendEmail(alert, subject, message);
    }

    public String sendEmail(Alerts alert, String subject, String htmlMessage) {
        try {

            log.info("Email Alert for " + alert.toString());

            String to = alert.getEmail();
            String from = appConfiguration.getEmailFrom();
            Properties props = System.getProperties();
            props.put("mail.smtp.host", appConfiguration.getEmailHost());
            props.put("mail.smtp.socketFactory.port", appConfiguration.getEmailSocketFactory());
            props.put("mail.smtp.socketFactory.class", appConfiguration.getEmailSocketFactoryClass());
            props.put("mail.smtp.auth", appConfiguration.getEmailAuth());
            props.put("mail.smtp.port", appConfiguration.getEmailSmtpPort());

            Session session = Session.getDefaultInstance(props,
                    new GMailAuthenticator(from, appConfiguration.getEmailFromPassword()));
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            if (appConfiguration.isDebugMode()) {
                message.addRecipient(Message.RecipientType.BCC,
                        new InternetAddress(appConfiguration.getDebugEmailId()));
            }

            message.setSubject(subject);
            message.setContent(htmlMessage, "text/html; charset=utf-8");
            Transport.send(message);

            log.info("Sent message successfully....");
        } catch (Exception mex) {
            log.error(mex.getMessage(), mex);
            mex.printStackTrace();
        }
        return "Email_Char_Length__" + htmlMessage.length();
    }

    public String sendEmail(Alerts alert, Set<AvlResponse> avlResponseList) {
        String subject = getSubjectLine(alert);
        String disableAlerts = appConfiguration.getAppHostNameURL() + "/api/Alerts/delete/" + alert.getId();
        String message = getHtmlVaccinationInfo(alert, avlResponseList, disableAlerts);
        if (null != message) {
            return sendEmail(alert, subject, message);
        }
        return null;
    }

    private String getSubjectLine(Alerts alert) {
        return "Vaccine Available Information Alert for  : " + alert.getName();
    }

    public String getHtmlVaccinationInfo(Alerts alert, Set<AvlResponse> avlResponseList, String disableAlertsURL) {

        String heading = "Vaccine Alert for " + alert.getName() + " having age group " + alert.getAge()
                + "+ listed below:";
        String updatedMessage = mainMessage.replace("${Alert}", heading);

        Iterator<AvlResponse> itr = avlResponseList.iterator();
        StringBuilder vcInfoBuilder = new StringBuilder();
        int totalSessions = 0;
        int sessionMoreThan10AVl = 0;

        while (itr.hasNext()) {
            AvlResponse res = itr.next();
            List<CowinResponseSessions> set = new ArrayList<>(res.getSessions());
            Collections.sort(set);

            String vaccineInfoMessage = vaccinationInfoMessage.replace("${VaccinationCenter}",
                    res.getCenterName() + " - " + res.getCenterAddress() + " - " + res.getPincode());

            if (null != set && set.size() > 0) {
                Iterator<CowinResponseSessions> itr2 = set.iterator();
                StringBuilder slotsAndCountBuilder = new StringBuilder();
                slotsAndCountBuilder.append("<ul>");

                while (itr2.hasNext()) {
                    CowinResponseSessions session = itr2.next();
                    if (vaccineInfoMessage.contains("${VaccineName}")) {
                        vaccineInfoMessage = vaccineInfoMessage.replace("${VaccineName}", session.getVaccine());
                    }
                    slotsAndCountBuilder.append("<li>");
                    slotsAndCountBuilder.append("<span>");
                    totalSessions++;

                    switch (alert.getDoseageType()) {
                    case 0:
                        sessionMoreThan10AVl += (session.getAvailable_capacity() > 10) ? 1 : 0;
                        slotsAndCountBuilder.append("Available Count For <b>Dose 1</b>: ")
                                .append(session.getAvailable_capacity_dose1())
                                .append(session.getAvailable_capacity_dose1() > 0
                                        && session.getAvailable_capacity_dose1() <= 10 ? " Hurry! " : "");
                        slotsAndCountBuilder.append(" And ");
                        slotsAndCountBuilder.append("Available Count For <b>Dose 2</b>: ")
                                .append(session.getAvailable_capacity_dose2())
                                .append(session.getAvailable_capacity_dose2() > 0
                                        && session.getAvailable_capacity_dose2() <= 10 ? " Hurry! " : "")
                                .append("\n");
                        break;
                    case 1:
                        sessionMoreThan10AVl += (session.getAvailable_capacity_dose1() > 10) ? 1 : 0;
                        slotsAndCountBuilder.append("Available Count For <b>Dose 1</b>: ")
                                .append(session.getAvailable_capacity_dose1())
                                .append(session.getAvailable_capacity_dose1() > 0
                                        && session.getAvailable_capacity_dose1() <= 10 ? " Hurry! " : "");
                        break;
                    case 2:
                        sessionMoreThan10AVl += (session.getAvailable_capacity_dose2() > 10) ? 1 : 0;
                        slotsAndCountBuilder.append("Available Count For <b>Dose 2</b>: ")
                                .append(session.getAvailable_capacity_dose2())
                                .append(session.getAvailable_capacity_dose2() > 0
                                        && session.getAvailable_capacity_dose2() <= 10 ? " Hurry! " : "");
                        break;
                    }

                    slotsAndCountBuilder.append(" on ");
                    slotsAndCountBuilder.append(session.getDate());
                    slotsAndCountBuilder.append(" for age group ");
                    slotsAndCountBuilder.append(session.getMin_age_limit());
                    slotsAndCountBuilder.append(" charged at ");
                    slotsAndCountBuilder.append(res.getFees());
                    slotsAndCountBuilder.append("</span>");
                    slotsAndCountBuilder.append("</li>");
                }

                slotsAndCountBuilder.append("</ul>");
                vaccineInfoMessage = vaccineInfoMessage.replace("${SlotAndCount}", slotsAndCountBuilder.toString());

                vcInfoBuilder.append(vaccineInfoMessage);
            }
        }

        log.info("For Alert: " + alert.getId() + " total sessions are " + totalSessions + " and more than 10 are: "
                + sessionMoreThan10AVl);
        if (sessionMoreThan10AVl == 0) {
            log.info("No Session more than 10 availability, not sending notification");
            return null;
        }

        String response = updatedMessage.replace("${AllVaccinationDetails}", vcInfoBuilder.toString());
        response = response.replace("${disableAlertURL}", disableAlertsURL);

        return response;
    }
}
