package com.prateek.cowinAvailibility.dto;

import com.prateek.cowinAvailibility.entity.Metrics;

public class MetricsDTO {
    private int dataLoadedFromAPI;
    private int dataLoadedFromCache;
    private int dataNotLoaded;
    private int slotAvailableCount;
    private int notificationEligibleCount;

    public int getDataLoadedFromAPI() {
        return dataLoadedFromAPI;
    }

    public void setDataLoadedFromAPI(int dataLoadedFromAPI) {
        this.dataLoadedFromAPI = dataLoadedFromAPI;
    }

    public int getDataLoadedFromCache() {
        return dataLoadedFromCache;
    }

    public void setDataLoadedFromCache(int dataLoadedFromCache) {
        this.dataLoadedFromCache = dataLoadedFromCache;
    }

    public int getDataNotLoaded() {
        return dataNotLoaded;
    }

    public void setDataNotLoaded(int dataNotLoaded) {
        this.dataNotLoaded = dataNotLoaded;
    }

    public int getSlotAvailableCount() {
        return slotAvailableCount;
    }

    public void setSlotAvailableCount(int slotAvailableCount) {
        this.slotAvailableCount = slotAvailableCount;
    }

    public int getNotificationEligibleCount() {
        return notificationEligibleCount;
    }

    public void setNotificationEligibleCount(int notificationEligibleCount) {
        this.notificationEligibleCount = notificationEligibleCount;
    }

    public void incrementDataLoadedFromAPI() {
        dataLoadedFromAPI += 1;
    }

    public void incrementDataLoadedFromCache() {
        dataLoadedFromCache += 1;
    }

    public void incrementDataNotLoaded() {
        dataNotLoaded += 1;
    }

    public void incrementslotAvailableCount() {
        slotAvailableCount += 1;
    }

    public void incrementNotificationEligibleCount() {
        notificationEligibleCount += 1;
    }

    @Override
    public String toString() {
        return "Metrics [dataLoadedFromAPI=" + dataLoadedFromAPI + ", dataLoadedFromCache=" + dataLoadedFromCache
                + ", dataNotLoaded=" + dataNotLoaded + ", notificationEligibleCount=" + notificationEligibleCount
                + ", slotAvailableCount=" + slotAvailableCount + "]";
    }

}