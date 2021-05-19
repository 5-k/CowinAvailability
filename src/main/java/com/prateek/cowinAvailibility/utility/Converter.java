package com.prateek.cowinAvailibility.utility;

import java.util.Iterator;

import com.prateek.cowinAvailibility.dto.cowinResponse.AvlResponse;
import com.prateek.cowinAvailibility.dto.cowinResponse.CowinResponseSessions;

public class Converter {

    public static String getVaccineAVLResponseString(AvlResponse avlResponse) {
        StringBuilder vaccineResponse = new StringBuilder();
        vaccineResponse.append("Vaccine is available as per your Alert Request");
        vaccineResponse.append("at center : ");
        vaccineResponse.append(avlResponse.getCenterName());
        vaccineResponse.append(" located at ");
        vaccineResponse.append(avlResponse.getCenterAddress());
        vaccineResponse.append(" ");
        vaccineResponse.append(avlResponse.getPincode());
        vaccineResponse.append(" for the following sessions ");

        if (avlResponse.getSessions() == null || avlResponse.getSessions().size() > 0) {
            return "";
        }

        Iterator<CowinResponseSessions> vldSessions = avlResponse.getSessions().iterator();
        while (vldSessions.hasNext()) {
            CowinResponseSessions session = vldSessions.next();
            if (session.getAvailable_capacity() > 0) {
                vaccineResponse.append("\n ");
                vaccineResponse.append(getVaccineAVLResponseString(session));
                vaccineResponse.append(" ");
            }
        }

        vaccineResponse.append("\n");
        return vaccineResponse.toString();
    }

    public static String getVaccineAVLResponseString(CowinResponseSessions cResponseSessions) {
        StringBuilder builder = new StringBuilder();
        builder.append("Slot - ");
        builder.append(cResponseSessions.getDate());
        builder.append(" for Type - ");
        builder.append(cResponseSessions.getVaccine());
        builder.append(" with quantity available - ");
        builder.append(cResponseSessions.getAvailable_capacity());
        builder.append(" for Age Limit - ");
        builder.append(cResponseSessions.getMin_age_limit());
        return builder.toString();
    }
}
