package com.courier.application;

import com.courier.domain.Location;

import java.util.HashMap;
import java.util.Map;

public class CourierState {
    private Location lastLocation;
    private double totalDistanceMeters;
    private final Map<String, Long> lastEntryTimes = new HashMap<>();

    public synchronized Location getLastLocation() {
        return lastLocation;
    }

    public synchronized void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    public synchronized void addDistance(double meters) {
        this.totalDistanceMeters += meters;
    }

    public synchronized double getTotalDistanceMeters() {
        return totalDistanceMeters;
    }

    public synchronized Long getLastEntryTime(String storeId) {
        return lastEntryTimes.get(storeId);
    }

    public synchronized void updateLastEntryTime(String storeId, long timeMillis) {
        lastEntryTimes.put(storeId, timeMillis);
    }
}
