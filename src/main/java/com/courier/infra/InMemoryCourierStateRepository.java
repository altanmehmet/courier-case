package com.courier.infra;

import com.courier.application.CourierState;
import com.courier.application.CourierStateRepository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryCourierStateRepository implements CourierStateRepository {
    private final ConcurrentMap<String, CourierState> states = new ConcurrentHashMap<>();

    @Override
    public CourierState getOrCreate(String courierId) {
        return states.computeIfAbsent(courierId, id -> new CourierState());
    }
}
