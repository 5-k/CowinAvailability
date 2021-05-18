package com.prateek.cowinAvailibility.enums;

public enum DoseType {

    First(1), Second(2), Any(0);

    public final int value;

    private DoseType(int value) {
        this.value = value;
    }

    public static DoseType from(int value) {
        for (DoseType dose : DoseType.values()) {
            if (dose.value == value) {
                return dose;
            }
        }
        return null;
    }

    public static int getValue(DoseType doseType) {
        if (null == doseType) {
            return -1;
        }
        return doseType.value;
    }
}
