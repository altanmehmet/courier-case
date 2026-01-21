package com.courier;

import com.courier.infra.MetricsRecorder;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MetricsRecorderTest {
    @Test
    void recordsCountersAndSummary() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        MetricsRecorder recorder = new MetricsRecorder(registry);

        recorder.recordLocationIngested();
        recorder.recordStoreEntryLogged();
        recorder.recordDistanceAdded(12.5);

        assertEquals(1.0, registry.get("courier.locations.ingested").counter().count(), 0.0001);
        assertEquals(1.0, registry.get("courier.store.entries.logged").counter().count(), 0.0001);
        assertEquals(12.5, registry.get("courier.distance.added.meters").summary().totalAmount(), 0.0001);
    }
}

