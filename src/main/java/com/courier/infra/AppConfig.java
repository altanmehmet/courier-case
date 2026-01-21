package com.courier.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.courier.application.CourierStateRepository;
import com.courier.application.CourierTrackingService;
import com.courier.application.DistanceAccumulatorListener;
import com.courier.application.EntryLogRepository;
import com.courier.application.LocationEventBus;
import com.courier.application.LocationListener;
import com.courier.application.StoreEntryConfig;
import com.courier.application.StoreEntryListener;
import com.courier.application.StoreRepository;
import com.courier.domain.DistanceCalculator;
import com.courier.domain.HaversineDistanceCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.List;

@Configuration
@EnableConfigurationProperties({StoreEntryProperties.class, SecurityProperties.class})
public class AppConfig {
    @Bean
    public DistanceCalculator distanceCalculator() {
        return new HaversineDistanceCalculator();
    }

    @Bean
    public StoreRepository storeRepository(ObjectMapper objectMapper) {
        return new JsonStoreRepository(objectMapper, "stores.json");
    }

    @Bean
    public MetricsRecorder metricsRecorder(MeterRegistry meterRegistry) {
        return new MetricsRecorder(meterRegistry);
    }

    @Bean
    public CourierStateRepository courierStateRepository() {
        return new InMemoryCourierStateRepository();
    }

    @Bean
    public EntryLogRepository entryLogRepository() {
        return new InMemoryEntryLogRepository();
    }

    @Bean
    public DistanceAccumulatorListener distanceAccumulatorListener(DistanceCalculator distanceCalculator, MetricsRecorder metricsRecorder) {
        return new DistanceAccumulatorListener(distanceCalculator, metricsRecorder);
    }

    @Bean
    public StoreEntryListener storeEntryListener(
            StoreRepository storeRepository,
            EntryLogRepository entryLogRepository,
            DistanceCalculator distanceCalculator,
            StoreEntryProperties storeEntryProperties,
            MetricsRecorder metricsRecorder
    ) {
        StoreEntryConfig config = new StoreEntryConfig(
                storeEntryProperties.getRadiusMeters(),
                storeEntryProperties.getCooldownMillis()
        );
        return new StoreEntryListener(storeRepository, entryLogRepository, distanceCalculator, config, metricsRecorder);
    }

    @Bean
    public LocationEventBus locationEventBus(List<LocationListener> listeners) {
        LocationEventBus eventBus = new LocationEventBus();
        listeners.forEach(eventBus::register);
        return eventBus;
    }

    @Bean
    public CourierTrackingService courierTrackingService(
            CourierStateRepository courierStateRepository,
            EntryLogRepository entryLogRepository,
            LocationEventBus eventBus,
            MetricsRecorder metricsRecorder
    ) {
        return new CourierTrackingService(courierStateRepository, entryLogRepository, eventBus, metricsRecorder);
    }
}
