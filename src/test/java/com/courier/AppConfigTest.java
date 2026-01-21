package com.courier;

import com.courier.application.CourierTrackingService;
import com.courier.application.DistanceAccumulatorListener;
import com.courier.application.LocationEventBus;
import com.courier.application.LocationListener;
import com.courier.application.StoreEntryListener;
import com.courier.application.StoreRepository;
import com.courier.domain.DistanceCalculator;
import com.courier.infra.AppConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppConfigTest {

    @Test
    void createsAllBeansAndWiresEventBusWithListeners() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.registerBean(ObjectMapper.class, (java.util.function.Supplier<ObjectMapper>) ObjectMapper::new);
        ctx.registerBean(SimpleMeterRegistry.class, SimpleMeterRegistry::new);
        ctx.register(AppConfig.class);
        ctx.refresh();

        DistanceCalculator calculator = ctx.getBean(DistanceCalculator.class);
        assertNotNull(calculator);

        StoreRepository storeRepository = ctx.getBean(StoreRepository.class);
        assertNotNull(storeRepository);
        assertTrue(storeRepository.findAll().size() > 0, "stores.json should load at least one store");

        DistanceAccumulatorListener distanceListener = ctx.getBean(DistanceAccumulatorListener.class);
        StoreEntryListener entryListener = ctx.getBean(StoreEntryListener.class);
        assertNotNull(distanceListener);
        assertNotNull(entryListener);

        List<LocationListener> listeners = ctx.getBeansOfType(LocationListener.class).values().stream().toList();
        assertTrue(listeners.size() >= 2);

        LocationEventBus bus = ctx.getBean(LocationEventBus.class);
        assertNotNull(bus);

        CourierTrackingService service = ctx.getBean(CourierTrackingService.class);
        assertNotNull(service);

        Object metricsRecorder = ctx.getBean("metricsRecorder");
        assertNotNull(metricsRecorder);

        ctx.close();
    }
}

