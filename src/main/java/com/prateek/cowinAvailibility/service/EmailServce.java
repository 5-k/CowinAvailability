package com.prateek.cowinAvailibility.service;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.prateek.cowinAvailibility.configuration.AppConfiguration;
import com.prateek.cowinAvailibility.dto.cowinResponse.AvlResponse;
import com.prateek.cowinAvailibility.dto.cowinResponse.CowinResponseSessions;
import com.prateek.cowinAvailibility.entity.Alerts;
import com.prateek.cowinAvailibility.utility.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class EmailServce {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final String mainMessage = "<div id=\":1cv\" class=\"Am Al editable LW-avf tS-tW tS-tY\" hidefocus=\"true\" aria-label=\"Message Body\" g_editable=\"true\" role=\"textbox\" aria-multiline=\"true\" contenteditable=\"true\" tabindex=\"1\" style=\"direction: ltr; min-height: 173px;\" itacorner=\"6,7:1,1,0,0\" spellcheck=\"true\">Hi There,<div><br></div><div>This is in reference with the alert setup by you mentioned below:</div><div><br></div>"
            + "<div><b>Alert </b>-&nbsp;${Alert}</div><div><br></div>"
            + "<div><b>Vaccine Availability:&nbsp;Yes</b><br clear=\"all\"><div><br></div><div><div></div>"
            + "${AllVaccinationDetails}" + "<div><br></div>" + "</div><div><br></div>"
            + "<div>To disable all alerts ,&nbsp;<a href=\"${disableAlertURL}\">Click Here</a></div><div><br></div>"
            + "<div>Get Vaccinated soon&nbsp;<img src=\"//ssl.gstatic.com/mail/emoji/v7/png48/emoji_u1f60e.png\" alt=\"\" goomoji=\"1f60e\" data-goomoji=\"1f60e\" style=\"margin: 0px 0.2ex; vertical-align: middle; height: 24px; width: 24px;\"></div>"
            + "<div><br>Regards</div>"
            + "<div><a href=\"https://www.linkedin.com/in/prateek-mishra-61aa4658/\">Connect on Linkedin</a></div>"
            + "</div>";

    private final String vaccinationInfoMessage = "<div>------------------------</div></div>"
            + "<div>Vaccination Center : ${VaccinationCenter}" + "<div>Vaccine :${VaccineName}</div>"
            + "<div>Available Vaccine :${SlotAndCount}</div>" + " <div>------------------------<br/></div>";

    @Autowired
    private AppConfiguration appConfiguration;

    public String sendEmail(Alerts alert, Set<AvlResponse> avlResponseList) {
        try {

            String disableAlerts = appConfiguration.getAppHostNameURL() + "/app/Alerts/delete/" + alert.getId();

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

            message.setSubject(getSubjectLine(alert));
            message.setContent(getHtmlVaccinationInfo(alert, avlResponseList, disableAlerts),
                    "text/html; charset=utf-8");
            Transport.send(message);

            log.info("Sent message successfully....");
        } catch (Exception mex) {
            log.error(mex.getMessage(), mex);
            mex.printStackTrace();
        }
        return "0";
    }

    private String getSubjectLine(Alerts alert) {
        return "Vaccine Available Information Alert for  : " + alert.getName();
    }

    public String getHtmlVaccinationInfo(Alerts alert, Set<AvlResponse> avlResponseList, String disableAlertsURL) {

        String updatedMessage = mainMessage.replace("${Alert}", alert.getName() + " - " + alert.getAge());

        Iterator<AvlResponse> itr = avlResponseList.iterator();
        StringBuilder vcInfoBuilder = new StringBuilder();

        while (itr.hasNext()) {
            AvlResponse res = itr.next();
            Set<CowinResponseSessions> set = res.getSessions();
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
                    slotsAndCountBuilder.append("Available Count: ");
                    slotsAndCountBuilder.append(session.getAvailable_capacity());
                    slotsAndCountBuilder.append(" at slot times :");
                    slotsAndCountBuilder.append(session.getSlots());
                    slotsAndCountBuilder.append("</li>");
                }

                slotsAndCountBuilder.append("</ul>");
                vaccineInfoMessage = vaccineInfoMessage.replace("${SlotAndCount}", slotsAndCountBuilder.toString());

                vcInfoBuilder.append(vaccineInfoMessage);
            }
        }

        String response = updatedMessage.replace("${AllVaccinationDetails}", vcInfoBuilder.toString());
        response = response.replace("${disableAlertURL}", disableAlertsURL);

        return response;
    }
}
