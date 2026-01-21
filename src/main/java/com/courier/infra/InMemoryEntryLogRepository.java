package com.courier.infra;

import com.courier.application.EntryLogRepository;
import com.courier.domain.StoreEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryEntryLogRepository implements EntryLogRepository {
    private final ConcurrentMap<String, List<StoreEntry>> entries = new ConcurrentHashMap<>();

    @Override
    public void add(StoreEntry entry) {
        entries.compute(entry.courierId(), (courierId, existing) -> {
            List<StoreEntry> list = existing == null ? new ArrayList<>() : new ArrayList<>(existing);
            list.add(entry);
            return Collections.unmodifiableList(list);
        });
    }

    @Override
    public List<StoreEntry> findByCourierId(String courierId) {
        return entries.getOrDefault(courierId, List.of());
    }
}
