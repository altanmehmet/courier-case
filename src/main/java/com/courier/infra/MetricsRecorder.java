package com.courier.infra;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;

public class MetricsRecorder {
    private final Counter locationsIngested;
    private final Counter storeEntriesLogged;
    private final DistributionSummary distanceAddedMeters;

    public MetricsRecorder(MeterRegistry meterRegistry) {
        this.locationsIngested = Counter.builder("courier.locations.ingested").register(meterRegistry);
        this.storeEntriesLogged = Counter.builder("courier.store.entries.logged").register(meterRegistry);
        this.distanceAddedMeters = DistributionSummary.builder("courier.distance.added.meters")
                .baseUnit("meters")
                .register(meterRegistry);
    }

    public void recordLocationIngested() {
        locationsIngested.increment();
    }

    public void recordStoreEntryLogged() {
        storeEntriesLogged.increment();
    }

    public void recordDistanceAdded(double meters) {
        distanceAddedMeters.record(meters);
    }
}

