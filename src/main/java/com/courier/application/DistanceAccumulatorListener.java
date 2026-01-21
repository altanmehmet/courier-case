package com.courier.application;

import com.courier.domain.DistanceCalculator;
import com.courier.domain.Location;

public class DistanceAccumulatorListener implements LocationListener {
    private final DistanceCalculator distanceCalculator;
    private final com.courier.infra.MetricsRecorder metricsRecorder;

    public DistanceAccumulatorListener(DistanceCalculator distanceCalculator, com.courier.infra.MetricsRecorder metricsRecorder) {
        this.distanceCalculator = distanceCalculator;
        this.metricsRecorder = metricsRecorder;
    }

    @Override
    public void onLocationIngested(LocationIngestedEvent event) {
        Location previous = event.previousLocation();
        if (previous == null) {
            return;
        }
        if (event.currentLocation().timeMillis() <= previous.timeMillis()) {
            return;
        }
        double distance = distanceCalculator.distanceMeters(previous, event.currentLocation());
        event.courierState().addDistance(distance);
        metricsRecorder.recordDistanceAdded(distance);
    }
}
