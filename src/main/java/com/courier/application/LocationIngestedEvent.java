package com.courier.application;

import com.courier.domain.Location;

public record LocationIngestedEvent(
        String courierId,
        Location currentLocation,
        Location previousLocation,
        CourierState courierState
) {
}
