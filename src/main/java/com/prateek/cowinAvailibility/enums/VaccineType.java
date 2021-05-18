package com.prateek.cowinAvailibility.enums;

public enum VaccineType {

    Covaxin("covaxin", "Covaxin"), Covishield("covishield", "Covishield"), sputnik("sputnik", "Sputnik V"),
    Any("any", "Any");

    public final String value;
    public final String displayName;

    private VaccineType(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public static VaccineType fromValue(String value) {
        for (VaccineType vaccineType : VaccineType.values()) {
            if (vaccineType.value.equalsIgnoreCase(value)) {
                return vaccineType;
            }
        }
        return null;
    }

    public static VaccineType fromDisplayName(String displayName) {
        for (VaccineType vaccineType : VaccineType.values()) {
            if (vaccineType.displayName.equalsIgnoreCase(displayName)) {
                return vaccineType;
            }
        }
        return null;
    }

}
