package com.courier.api;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record LocationRequest(
        @NotBlank String courierId,
        @Min(1) long timeMillis,
        @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0") double lat,
        @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0") double lng
) {
}
