package com.courier;

import com.courier.api.LocationRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocationRequestValidationTest {
    @Test
    void rejectsOutOfRangeLatitude() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        LocationRequest request = new LocationRequest("courier-1", 1_000L, 120.0, 29.0);

        boolean hasViolations = !validator.validate(request).isEmpty();

        assertTrue(hasViolations);
    }

    @Test
    void acceptsValidCoordinates() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        LocationRequest request = new LocationRequest("courier-1", 1_000L, 40.0, 29.0);

        boolean hasViolations = !validator.validate(request).isEmpty();

        assertFalse(hasViolations);
    }
}
