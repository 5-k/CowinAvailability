package com.prateek.cowinAvailibility.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "metrics")
public class Metrics {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "data_loaded_from_api")
    private int dataLoadedFromAPI;

    @Column(name = "data_loaded_from_cache")
    private int dataLoadedFromCache;

    @Column(name = "data_not_loaded")
    private int dataNotLoaded;

    @Column(name = "slot_available_count")
    private int slotAvailableCount;

    @Column(name = "notification_eligible_count")
    private int notificationEligibleCount;

    @Column(name = "star_time")
    private Date startTime;
    @Column(name = "end_time")
    private Date endTime;

    public Metrics() {
    }

    public Metrics(int dataLoadedFromAPI, int dataLoadedFromCache, int dataNotLoaded, int slotAvailableCount,
            int notificationEligibleCount) {
        this.dataLoadedFromAPI = dataLoadedFromAPI;
        this.dataLoadedFromCache = dataLoadedFromCache;
        this.dataNotLoaded = dataNotLoaded;
        this.slotAvailableCount = slotAvailableCount;
        this.notificationEligibleCount = notificationEligibleCount;
    }

    public Metrics(int id, int dataLoadedFromAPI, int dataLoadedFromCache, int dataNotLoaded, int slotAvailableCount,
            int notificationEligibleCount, Date startTime, Date endTime) {
        this.id = id;
        this.dataLoadedFromAPI = dataLoadedFromAPI;
        this.dataLoadedFromCache = dataLoadedFromCache;
        this.dataNotLoaded = dataNotLoaded;
        this.slotAvailableCount = slotAvailableCount;
        this.notificationEligibleCount = notificationEligibleCount;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

}
