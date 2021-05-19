package com.prateek.cowinAvailibility.utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.prateek.cowinAvailibility.dto.MetricsDTO;
import com.prateek.cowinAvailibility.dto.cowinResponse.AvlResponse;
import com.prateek.cowinAvailibility.dto.cowinResponse.CowinResponse;
import com.prateek.cowinAvailibility.dto.cowinResponse.CowinResponseCenter;
import com.prateek.cowinAvailibility.dto.cowinResponse.CowinResponseSessions;
import com.prateek.cowinAvailibility.dto.cowinResponse.CowinVaccineFees;
import com.prateek.cowinAvailibility.entity.Alerts;
import com.prateek.cowinAvailibility.entity.Metrics;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.util.CollectionUtils;

public class Utils {

    public static String formatStateData(List<Map<String, Integer>> list) {
        StringBuilder builder = new StringBuilder();
        String preText = "Select state: \n\n";
        builder.append(preText);

        for (int i = 0; i < list.size(); i++) {
            formatStateOrCity((Map<String, Integer>) list.get(i), builder);
        }
        return builder.toString();
    }

    public static String formatCityData(Map<String, Integer> map) {
        StringBuilder builder = new StringBuilder();
        String preText = "Select city: \n\n";
        builder.append(preText);
        return formatStateOrCity(map, builder);
    }

    private static String formatStateOrCity(Map<String, Integer> map, StringBuilder builder) {
        Map<String, Integer> sortedMap = new TreeMap<String, Integer>(map);
        Set<Map.Entry<String, Integer>> set = sortedMap.entrySet();
        Iterator<Map.Entry<String, Integer>> itr = set.iterator();
        while (itr.hasNext()) {
            Map.Entry<String, Integer> entry = itr.next();
            builder.append(entry.getKey());
            builder.append("\n\n");
        }
        return builder.toString();
    }

    public static List<String> getStateList(List<Map<String, Integer>> list) {
        List<String> states = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            Set<Map.Entry<String, Integer>> set = ((Map<String, Integer>) list.get(i)).entrySet();
            Iterator<Map.Entry<String, Integer>> itr = set.iterator();
            while (itr.hasNext()) {
                Map.Entry<String, Integer> entry = itr.next();
                states.add(entry.getKey().toLowerCase());
            }
        }
        return states;
    }

    public static List<String> getCityList(Map<String, Map<String, Integer>> map) {
        List<String> cityList = new ArrayList<>();

        for (int i = 0; i < map.size(); i++) {
            Set<Map.Entry<String, Map<String, Integer>>> set = ((Map<String, Map<String, Integer>>) map).entrySet();

            Iterator<Map.Entry<String, Map<String, Integer>>> itr = set.iterator();
            while (itr.hasNext()) {
                Map.Entry<String, Map<String, Integer>> entry = itr.next();
                Map<String, Integer> innerMap = entry.getValue();
                Set<Map.Entry<String, Integer>> innerSet = innerMap.entrySet();

                Iterator<Map.Entry<String, Integer>> innerIterator = innerSet.iterator();
                while (innerIterator.hasNext()) {
                    Map.Entry<String, Integer> innerEntry = innerIterator.next();
                    cityList.add(innerEntry.getKey().toLowerCase());
                }
            }
        }

        return cityList;
    }

    public static boolean isPinCodeValid(String pincode) {
        try {
            int pin = Integer.parseInt(pincode);
            return pin >= 100000 && pin <= 999999;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int getDistrictId(Map<String, Map<String, Integer>> map, String cityName) {

        for (int i = 0; i < map.size(); i++) {
            Set<Map.Entry<String, Map<String, Integer>>> set = ((Map<String, Map<String, Integer>>) map).entrySet();
            Iterator<Map.Entry<String, Map<String, Integer>>> itr = set.iterator();

            while (itr.hasNext()) {
                Map.Entry<String, Map<String, Integer>> entry = itr.next();
                Map<String, Integer> innerMap = new HashMapCaseInsensitive<String, Integer>(entry.getValue());
                if (innerMap.containsKey(cityName)) {
                    return innerMap.get(cityName);
                }
            }
        }

        return 0;
    }

    public static String getEmotionLessString(String message) {
        if (null == message) {
            return "";
        }

        return message.replaceAll(Constants.characterFilter, "__EMOJI__");
    }

    public static List<String> splitToNChar(String text, int size) {

        List<String> parts = new ArrayList<>();
        if (null == text) {
            return parts;
        }

        int length = text.length();
        for (int i = 0; i < length; i += size) {
            parts.add(text.substring(i, Math.min(length, i + size)));
        }
        return parts;
        // return parts.toArray(new String[0]);
    }

    public static Date getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal.getTime();
    }

    public static Calendar getCalender() {
        Calendar cal = Calendar.getInstance();

        return cal;
    }

    public static Set<AvlResponse> processResponse(Alerts alert, CowinResponse response, Logger log) {
        Set<AvlResponse> avlResponseList = new LinkedHashSet<AvlResponse>();
        if (null == response) {
            return avlResponseList;
        }

        for (int i = 0; i < response.getCenters().size(); i++) {
            CowinResponseCenter center = response.getCenters().get(i);
            Set<CowinResponseSessions> validSessions = new LinkedHashSet<CowinResponseSessions>();
            String vaccineType = "";

            for (int j = 0; j < center.getSessions().size(); j++) {

                CowinResponseSessions session = center.getSessions().get(j);
                vaccineType = session.getVaccine();
                if ((alert.getVaccineType().trim().equalsIgnoreCase(Constants.VACCINE_TYPE_ANY)
                        || alert.getVaccineType().trim().equalsIgnoreCase(session.getVaccine().trim()))
                        && (alert.getAge() == session.getMin_age_limit())
                        && (session.getAvailable_capacity() > 0 || session.getAvailable_capacity_dose1() > 0
                                || session.getAvailable_capacity_dose2() > 0)) {

                    if (alert.getDoseageType() == 1) {
                        if (session.getAvailable_capacity_dose1() > 1) {
                            validSessions.add(session);
                        } else {
                            log.debug("DoseType 1 not found for center" + center.toString() + " session: "
                                    + session.toString());
                        }
                    } else if (alert.getDoseageType() == 2) {
                        if (session.getAvailable_capacity_dose2() > 1) {
                            validSessions.add(session);
                        } else {
                            log.debug("DoseType 2 not found for center" + center.toString() + " session: "
                                    + session.toString());
                        }
                    } else if (alert.getDoseageType() == 0) {
                        if (session.getAvailable_capacity() > 1) {
                            validSessions.add(session);
                        } else {
                            log.debug("Dosage not found or less for center " + center.toString() + " WITH SESSION "
                                    + session.toString() + " has vaccine count for both "
                                    + session.getAvailable_capacity());
                        }
                    }

                } else {
                    /*
                     * log.debug("Coditions not matched for alert id " + alert.getId() +
                     * " with vaccine type" + alert.getVaccineType() + " and session vaccine type "
                     * + session.getVaccine() + " age alert age = " + alert.getAge() +
                     * " and session age = " + session.getMin_age_limit() + " and capacity " +
                     * session.getAvailable_capacity());
                     */
                }
            }

            if (validSessions.size() > 0) {
                AvlResponse avlResponse = new AvlResponse(center.getCenter_id(), center.getName(), center.getAddress(),
                        center.getPincode(), validSessions);

                if (center.getFee_type().equalsIgnoreCase("paid")
                        && !CollectionUtils.isEmpty(center.getVaccineFees())) {
                    for (int k = 0; k < center.getVaccineFees().size(); k++) {
                        CowinVaccineFees fees = center.getVaccineFees().get(i);
                        if (null != fees && fees.getVaccine().trim().equalsIgnoreCase(vaccineType)) {
                            avlResponse.setFees(fees.getFees());
                            break;
                        } else {
                            avlResponse.setFees(center.getFee_type());
                        }
                    }
                } else {
                    avlResponse.setFees(center.getFee_type());
                }

                avlResponseList.add(avlResponse);
            }
        }

        if (avlResponseList.size() == 0) {
            log.debug("No avlResponseList Session found for Alert " + alert);
        }

        return avlResponseList;
    }

    public static String getTelegramAlertMessage(Alerts alert, Set<AvlResponse> avlResponseList) {
        if (null == avlResponseList || avlResponseList.size() == 0) {
            return null;
        }

        StringBuilder updatedMessage = new StringBuilder();
        updatedMessage.append("Hi ");
        if (null != alert.getName()) {
            updatedMessage.append(StringUtils.capitalize(alert.getName()));
        }
        updatedMessage.append(", following slots are available as per your alert: ");
        updatedMessage.append(alert.getAge());
        updatedMessage.append("+");

        if (alert.isPinCodeSearch()) {
            updatedMessage.append(" for pincode: ");
            updatedMessage.append(alert.getPincode());
        } else {
            if (null != alert.getCity() && null != alert.getState()) {
                updatedMessage.append(" for ");
                updatedMessage.append(StringUtils.capitalize(alert.getCity()));
                updatedMessage.append(", ");
                updatedMessage.append(org.apache.commons.lang3.StringUtils.capitalize(alert.getState()));
            }
        }

        updatedMessage.append(" and vaccine type: ").append(alert.getVaccineType());
        updatedMessage.append(" for dose type: ")
                .append(alert.getDoseageType() == 0 ? "Any/Both" : "Dose " + alert.getDoseageType());
        updatedMessage.append("\n");

        if (avlResponseList.size() > 12) {
            updatedMessage.append("\n");
            updatedMessage.append("More than 12 centers are avaialble for this alert.");
            updatedMessage.append("\n\n");
        }

        Iterator<AvlResponse> itr = avlResponseList.iterator();
        int i = 0;
        while (itr.hasNext()) {
            i++;
            AvlResponse res = itr.next();
            Set<CowinResponseSessions> set = res.getSessions();
            updatedMessage.append("\n\n");
            updatedMessage.append("🚑").append(res.getCenterName()).append(" - ").append(res.getCenterAddress())
                    .append("-").append(res.getPincode()).append("\n");

            if (null != set && set.size() > 0) {
                Iterator<CowinResponseSessions> itr2 = set.iterator();
                int j = 0;

                while (itr2.hasNext()) {
                    j++;
                    if (j == 1) {
                        CowinResponseSessions session = itr2.next();
                        updatedMessage.append("-------------------\n");
                        updatedMessage.append("Type: ").append(session.getVaccine()).append("\n");
                        updatedMessage.append("Date: ").append(session.getDate()).append("\n");
                        updatedMessage.append("Age: ").append(session.getMin_age_limit()).append("\n");
                        updatedMessage.append("Fee: ").append(res.getFees()).append("\n");

                        switch (alert.getDoseageType()) {
                        case 0:
                            updatedMessage.append("Available Count For Dose 1: ")
                                    .append(session.getAvailable_capacity_dose1())
                                    .append(session.getAvailable_capacity_dose1() <= 10 ? " Hurry! " : "").append("\n");
                            updatedMessage.append("Available Count For Dose 2: ")
                                    .append(session.getAvailable_capacity_dose2())
                                    .append(session.getAvailable_capacity_dose2() <= 10 ? " Hurry! " : "").append("\n");
                            break;
                        case 1:
                            updatedMessage.append("Available Count For Dose 1: ")
                                    .append(session.getAvailable_capacity_dose1())
                                    .append(session.getAvailable_capacity_dose1() <= 10 ? " Hurry! " : "").append("\n");
                            break;
                        case 2:
                            updatedMessage.append("Available Count For Dose 2: ")
                                    .append(session.getAvailable_capacity_dose2())
                                    .append(session.getAvailable_capacity_dose2() <= 10 ? " Hurry! " : "").append("\n");
                            break;
                        }
                        updatedMessage.append("-------------------");
                    } else {
                        CowinResponseSessions session = itr2.next();
                        updatedMessage.append("\n-------------------\n");
                        updatedMessage.append(session.getAvailable_capacity()).append(" more available on  ")
                                .append(session.getDate()).append("\n");
                        updatedMessage.append("-------------------");
                    }

                }
                updatedMessage.append("\n");
            }

            if (i > 12) {
                updatedMessage.append("\n").append(avlResponseList.size() - 12)
                        .append(" more available option(s), not added to this message.\nPlease check the Cowin Portal");
                break;
            }
        }

        updatedMessage.append("\n\n");
        updatedMessage.append(
                "Click here to stop recieving updates for this alert:  /stopUpdatesForAlert" + alert.getId() + " \n");
        updatedMessage.append("Click here to stop recieving updates for all alerts:  /stopUpdates \n");
        updatedMessage.append("Click fetch Latest Update on this:  /fetchLatestUpdateFor" + alert.getId());
        updatedMessage.append("\n\nClick view updates to see all updates set by you:  /viewAlerts");
        updatedMessage.append("\n\nFound a slot? Book it now at \nhttps://selfregistration.cowin.gov.in/ ");

        return updatedMessage.toString();
    }

    public static Metrics fromMetricDto(MetricsDTO metricsDTO) {
        return new Metrics(metricsDTO.getDataLoadedFromAPI(), metricsDTO.getDataLoadedFromCache(),
                metricsDTO.getDataNotLoaded(), metricsDTO.getSlotAvailableCount(),
                metricsDTO.getNotificationEligibleCount());
    }
}
