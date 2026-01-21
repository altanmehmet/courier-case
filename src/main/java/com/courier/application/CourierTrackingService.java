package com.courier.application;

import com.courier.domain.Location;
import com.courier.domain.StoreEntry;

import java.util.List;

public class CourierTrackingService {
    private final CourierStateRepository courierStateRepository;
    private final EntryLogRepository entryLogRepository;
    private final LocationEventBus eventBus;
    private final com.courier.infra.MetricsRecorder metricsRecorder;

    public CourierTrackingService(
            CourierStateRepository courierStateRepository,
            EntryLogRepository entryLogRepository,
            LocationEventBus eventBus,
            com.courier.infra.MetricsRecorder metricsRecorder
    ) {
        this.courierStateRepository = courierStateRepository;
        this.entryLogRepository = entryLogRepository;
        this.eventBus = eventBus;
        this.metricsRecorder = metricsRecorder;
    }

    public void ingestLocation(LocationUpdate update) {
        CourierState state = courierStateRepository.getOrCreate(update.courierId());
        Location previous = state.getLastLocation();
        Location current = update.location();
        eventBus.publish(new LocationIngestedEvent(update.courierId(), current, previous, state));
        state.setLastLocation(current);
        metricsRecorder.recordLocationIngested();
    }

    public double getTotalDistanceMeters(String courierId) {
        return courierStateRepository.getOrCreate(courierId).getTotalDistanceMeters();
    }

    public List<StoreEntry> getEntries(String courierId) {
        return entryLogRepository.findByCourierId(courierId);
    }
}
