package com.courier;

import com.courier.application.CourierState;
import com.courier.application.LocationEventBus;
import com.courier.application.LocationIngestedEvent;
import com.courier.application.LocationListener;
import com.courier.domain.Location;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocationEventBusTest {

    @Test
    void publishNotifiesAllRegisteredListenersInRegistrationOrder() {
        LocationEventBus bus = new LocationEventBus();
        List<String> calls = new ArrayList<>();

        LocationListener l1 = event -> calls.add("l1:" + event.courierId());
        LocationListener l2 = event -> calls.add("l2:" + event.courierId());

        bus.register(l1);
        bus.register(l2);

        Location location = new Location(1L, 0.0, 0.0);
        bus.publish(new LocationIngestedEvent("courier-1", location, null, new CourierState()));

        assertEquals(List.of("l1:courier-1", "l2:courier-1"), calls);
    }
}

