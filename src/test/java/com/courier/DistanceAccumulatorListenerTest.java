package com.courier;

import com.courier.application.CourierState;
import com.courier.application.DistanceAccumulatorListener;
import com.courier.application.LocationIngestedEvent;
import com.courier.domain.HaversineDistanceCalculator;
import com.courier.domain.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DistanceAccumulatorListenerTest {
    @Test
    void ignoresFirstLocationWhenNoPrevious() {
        DistanceAccumulatorListener listener = new DistanceAccumulatorListener(new HaversineDistanceCalculator(), new com.courier.infra.MetricsRecorder(new io.micrometer.core.instrument.simple.SimpleMeterRegistry()));
        CourierState state = new CourierState();
        Location current = new Location(1_000L, 40.0, 29.0);

        listener.onLocationIngested(new LocationIngestedEvent("courier-1", current, null, state));

        assertEquals(0.0, state.getTotalDistanceMeters(), 0.0001);
    }

    @Test
    void ignoresOutOfOrderLocations() {
        DistanceAccumulatorListener listener = new DistanceAccumulatorListener(new HaversineDistanceCalculator(), new com.courier.infra.MetricsRecorder(new io.micrometer.core.instrument.simple.SimpleMeterRegistry()));
        CourierState state = new CourierState();
        Location previous = new Location(2_000L, 40.0, 29.0);
        Location current = new Location(1_000L, 40.0001, 29.0001);

        listener.onLocationIngested(new LocationIngestedEvent("courier-1", current, previous, state));

        assertEquals(0.0, state.getTotalDistanceMeters(), 0.0001);
    }

    @Test
    void accumulatesDistanceForInOrderLocations() {
        DistanceAccumulatorListener listener = new DistanceAccumulatorListener(new HaversineDistanceCalculator(), new com.courier.infra.MetricsRecorder(new io.micrometer.core.instrument.simple.SimpleMeterRegistry()));
        CourierState state = new CourierState();
        Location previous = new Location(1_000L, 40.0, 29.0);
        Location current = new Location(2_000L, 40.0001, 29.0001);

        listener.onLocationIngested(new LocationIngestedEvent("courier-1", current, previous, state));

        double total = state.getTotalDistanceMeters();
        assertTrue(total > 0.0, "distance should be added for increasing timestamps");
    }
}
