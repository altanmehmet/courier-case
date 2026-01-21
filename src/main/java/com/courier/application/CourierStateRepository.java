package com.courier.application;

public interface CourierStateRepository {
    CourierState getOrCreate(String courierId);
}
