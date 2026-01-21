package com.courier.application;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LocationEventBus {
    private final List<LocationListener> listeners = new CopyOnWriteArrayList<>();

    public void register(LocationListener listener) {
        listeners.add(listener);
    }

    public void publish(LocationIngestedEvent event) {
        for (LocationListener listener : listeners) {
            listener.onLocationIngested(event);
        }
    }
}
