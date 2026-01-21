package com.courier;

import com.courier.application.CourierState;
import com.courier.application.CourierStateRepository;
import com.courier.application.CourierTrackingService;
import com.courier.application.EntryLogRepository;
import com.courier.application.LocationEventBus;
import com.courier.application.LocationIngestedEvent;
import com.courier.domain.Location;
import com.courier.domain.StoreEntry;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CourierTrackingServiceTest {

    @Test
    void ingestLocationPublishesEventWithPreviousAndUpdatesLastLocationAfterPublish() {
        CourierState state = new CourierState();
        CourierStateRepository courierStateRepository = courierId -> state;

        EntryLogRepository entryLogRepository = new EntryLogRepository() {
            @Override
            public void add(StoreEntry entry) {
                throw new UnsupportedOperationException("not needed");
            }

            @Override
            public List<StoreEntry> findByCourierId(String courierId) {
                return List.of();
            }
        };

        LocationEventBus eventBus = new LocationEventBus();
        AtomicReference<LocationIngestedEvent> seen = new AtomicReference<>();
        eventBus.register(event -> {
            // At publish time, lastLocation must still reflect "previous" (or null on first ingest)
            seen.set(event);
            assertSame(state, event.courierState());
            assertEquals(event.previousLocation(), state.getLastLocation());
        });

        CourierTrackingService service = new CourierTrackingService(
                courierStateRepository,
                entryLogRepository,
                eventBus,
                new com.courier.infra.MetricsRecorder(new io.micrometer.core.instrument.simple.SimpleMeterRegistry())
        );

        Location first = new Location(1000L, 40.0, 29.0);
        service.ingestLocation(new com.courier.application.LocationUpdate("courier-1", first));
        assertNotNull(seen.get());
        assertNull(seen.get().previousLocation());
        assertSame(first, state.getLastLocation());

        Location second = new Location(2000L, 40.0001, 29.0001);
        service.ingestLocation(new com.courier.application.LocationUpdate("courier-1", second));
        assertEquals(first, seen.get().previousLocation());
        assertSame(second, state.getLastLocation());
    }

    @Test
    void getTotalDistanceMetersDelegatesToStateRepository() {
        CourierState state = new CourierState();
        state.addDistance(12.34);
        EntryLogRepository entryLogRepository = new EntryLogRepository() {
            @Override
            public void add(StoreEntry entry) {
                throw new UnsupportedOperationException("not needed");
            }

            @Override
            public List<StoreEntry> findByCourierId(String courierId) {
                return List.of();
            }
        };
        CourierTrackingService service = new CourierTrackingService(
                courierId -> state,
                entryLogRepository,
                new LocationEventBus(),
                new com.courier.infra.MetricsRecorder(new io.micrometer.core.instrument.simple.SimpleMeterRegistry())
        );

        assertEquals(12.34, service.getTotalDistanceMeters("courier-1"), 0.00001);
    }

    @Test
    void getEntriesDelegatesToEntryLogRepository() {
        List<StoreEntry> entries = new ArrayList<>();
        entries.add(new StoreEntry("courier-1", "Test Store", 1L));
        EntryLogRepository repo = new EntryLogRepository() {
            @Override
            public void add(StoreEntry entry) {
                entries.add(entry);
            }

            @Override
            public List<StoreEntry> findByCourierId(String courierId) {
                return List.copyOf(entries);
            }
        };

        CourierTrackingService service = new CourierTrackingService(
                courierId -> new CourierState(),
                repo,
                new LocationEventBus(),
                new com.courier.infra.MetricsRecorder(new io.micrometer.core.instrument.simple.SimpleMeterRegistry())
        );
        List<StoreEntry> result = service.getEntries("courier-1");
        assertEquals(1, result.size());
        assertEquals("Test Store", result.get(0).storeName());
        assertTrue(result instanceof List);
    }
}

