package com.courier;

import com.courier.domain.HaversineDistanceCalculator;
import com.courier.domain.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HaversineDistanceCalculatorTest {
    @Test
    void returnsZeroForSamePoint() {
        HaversineDistanceCalculator calculator = new HaversineDistanceCalculator();
        Location point = new Location(0L, 40.0, 29.0);
        double distance = calculator.distanceMeters(point, point);
        assertTrue(distance < 0.0001);
    }
}
