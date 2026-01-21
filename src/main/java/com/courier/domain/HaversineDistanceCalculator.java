package com.courier.domain;

public class HaversineDistanceCalculator implements DistanceCalculator {
    private static final double EARTH_RADIUS_M = 6371000.0;

    @Override
    public double distanceMeters(Location from, Location to) {
        double lat1 = Math.toRadians(from.lat());
        double lat2 = Math.toRadians(to.lat());
        double deltaLat = Math.toRadians(to.lat() - from.lat());
        double deltaLng = Math.toRadians(to.lng() - from.lng());

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_M * c;
    }
}
