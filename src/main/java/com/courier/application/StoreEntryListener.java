package com.courier.application;

import com.courier.domain.DistanceCalculator;
import com.courier.domain.Location;
import com.courier.domain.Store;
import com.courier.domain.StoreEntry;

import java.util.List;

public class StoreEntryListener implements LocationListener {
    private final StoreRepository storeRepository;
    private final EntryLogRepository entryLogRepository;
    private final DistanceCalculator distanceCalculator;
    private final StoreEntryConfig config;
    private final com.courier.infra.MetricsRecorder metricsRecorder;

    public StoreEntryListener(
            StoreRepository storeRepository,
            EntryLogRepository entryLogRepository,
            DistanceCalculator distanceCalculator,
            StoreEntryConfig config,
            com.courier.infra.MetricsRecorder metricsRecorder
    ) {
        this.storeRepository = storeRepository;
        this.entryLogRepository = entryLogRepository;
        this.distanceCalculator = distanceCalculator;
        this.config = config;
        this.metricsRecorder = metricsRecorder;
    }

    @Override
    public void onLocationIngested(LocationIngestedEvent event) {
        Location current = event.currentLocation();
        List<Store> stores = storeRepository.findAll();
        for (Store store : stores) {
            Location storeLocation = new Location(current.timeMillis(), store.lat(), store.lng());
            double distance = distanceCalculator.distanceMeters(current, storeLocation);
            if (distance <= config.entryRadiusMeters() && isEligible(event, store.id())) {
                entryLogRepository.add(new StoreEntry(event.courierId(), store.name(), current.timeMillis()));
                event.courierState().updateLastEntryTime(store.id(), current.timeMillis());
                if (metricsRecorder != null) {
                    metricsRecorder.recordStoreEntryLogged();
                }
            }
        }
    }

    private boolean isEligible(LocationIngestedEvent event, String storeId) {
        Long lastEntry = event.courierState().getLastEntryTime(storeId);
        if (lastEntry == null) {
            return true;
        }
        return event.currentLocation().timeMillis() - lastEntry > config.reentryCooldownMillis();
    }
}
