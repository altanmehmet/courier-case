package com.courier;

import com.courier.application.CourierState;
import com.courier.application.EntryLogRepository;
import com.courier.application.LocationIngestedEvent;
import com.courier.application.StoreEntryListener;
import com.courier.application.StoreRepository;
import com.courier.domain.HaversineDistanceCalculator;
import com.courier.domain.Location;
import com.courier.domain.Store;
import com.courier.domain.StoreEntry;
import com.courier.infra.InMemoryEntryLogRepository;
import com.courier.infra.MetricsRecorder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StoreEntryListenerTest {
    @Test
    void ignoresReentryWithinOneMinute() {
        StoreRepository storeRepository = () -> List.of(new Store("test-store", "Test Store", 40.0, 29.0));
        EntryLogRepository entryLogRepository = new InMemoryEntryLogRepository();
        StoreEntryListener listener = new StoreEntryListener(
                storeRepository,
                entryLogRepository,
                new HaversineDistanceCalculator(),
                com.courier.application.StoreEntryConfig.defaults(),
                new MetricsRecorder(new io.micrometer.core.instrument.simple.SimpleMeterRegistry())
        );
        CourierState state = new CourierState();

        Location first = new Location(1_000L, 40.0, 29.0);
        listener.onLocationIngested(new LocationIngestedEvent("courier-1", first, null, state));

        Location reentry = new Location(1_050L, 40.0, 29.0);
        listener.onLocationIngested(new LocationIngestedEvent("courier-1", reentry, first, state));

        List<StoreEntry> entries = entryLogRepository.findByCourierId("courier-1");
        assertEquals(1, entries.size());
    }

    @Test
    void countsReentryAfterOneMinute() {
        StoreRepository storeRepository = () -> List.of(new Store("test-store", "Test Store", 40.0, 29.0));
        EntryLogRepository entryLogRepository = new InMemoryEntryLogRepository();
        StoreEntryListener listener = new StoreEntryListener(
                storeRepository,
                entryLogRepository,
                new HaversineDistanceCalculator(),
                com.courier.application.StoreEntryConfig.defaults(),
                new com.courier.infra.MetricsRecorder(new io.micrometer.core.instrument.simple.SimpleMeterRegistry())
        );
        CourierState state = new CourierState();

        Location first = new Location(1_000L, 40.0, 29.0);
        listener.onLocationIngested(new LocationIngestedEvent("courier-1", first, null, state));

        Location reentry = new Location(61_001L, 40.0, 29.0);
        listener.onLocationIngested(new LocationIngestedEvent("courier-1", reentry, first, state));

        List<StoreEntry> entries = entryLogRepository.findByCourierId("courier-1");
        assertEquals(2, entries.size());
    }

    @Test
    void logsEntryWithinHundredMeters() {
        StoreRepository storeRepository = () -> List.of(new Store("test-store", "Test Store", 0.0, 0.0));
        EntryLogRepository entryLogRepository = new InMemoryEntryLogRepository();
        StoreEntryListener listener = new StoreEntryListener(
                storeRepository,
                entryLogRepository,
                new HaversineDistanceCalculator(),
                com.courier.application.StoreEntryConfig.defaults(),
                new com.courier.infra.MetricsRecorder(new io.micrometer.core.instrument.simple.SimpleMeterRegistry())
        );
        CourierState state = new CourierState();

        Location near99m = new Location(1_000L, 0.00089, 0.0);
        listener.onLocationIngested(new LocationIngestedEvent("courier-1", near99m, null, state));

        List<StoreEntry> entries = entryLogRepository.findByCourierId("courier-1");
        assertEquals(1, entries.size(), "entry should be logged when ~100m inside radius");
    }

    @Test
    void ignoresEntryOutsideHundredMeters() {
        StoreRepository storeRepository = () -> List.of(new Store("test-store", "Test Store", 0.0, 0.0));
        EntryLogRepository entryLogRepository = new InMemoryEntryLogRepository();
        StoreEntryListener listener = new StoreEntryListener(
                storeRepository,
                entryLogRepository,
                new HaversineDistanceCalculator(),
                com.courier.application.StoreEntryConfig.defaults(),
                new com.courier.infra.MetricsRecorder(new io.micrometer.core.instrument.simple.SimpleMeterRegistry())
        );
        CourierState state = new CourierState();

        Location beyond102m = new Location(1_000L, 0.00092, 0.0);
        listener.onLocationIngested(new LocationIngestedEvent("courier-1", beyond102m, null, state));

        List<StoreEntry> entries = entryLogRepository.findByCourierId("courier-1");
        assertEquals(0, entries.size(), "entry should not be logged when outside 100m");
    }

    @Test
    void worksWhenMetricsRecorderIsNull() {
        StoreRepository storeRepository = () -> List.of(new Store("test-store", "Test Store", 0.0, 0.0));
        EntryLogRepository entryLogRepository = new InMemoryEntryLogRepository();
        StoreEntryListener listener = new StoreEntryListener(
                storeRepository,
                entryLogRepository,
                new HaversineDistanceCalculator(),
                com.courier.application.StoreEntryConfig.defaults(),
                null
        );
        CourierState state = new CourierState();

        Location inside = new Location(1_000L, 0.00089, 0.0);
        listener.onLocationIngested(new LocationIngestedEvent("courier-1", inside, null, state));

        List<StoreEntry> entries = entryLogRepository.findByCourierId("courier-1");
        assertEquals(1, entries.size());
    }
}
