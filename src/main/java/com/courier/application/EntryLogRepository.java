package com.courier.application;

import com.courier.domain.StoreEntry;

import java.util.List;

public interface EntryLogRepository {
    void add(StoreEntry entry);

    List<StoreEntry> findByCourierId(String courierId);
}
