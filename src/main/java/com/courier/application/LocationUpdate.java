package com.courier.application;

import com.courier.domain.Location;

public record LocationUpdate(String courierId, Location location) {
}
