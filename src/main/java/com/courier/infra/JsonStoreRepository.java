package com.courier.infra;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.courier.application.StoreRepository;
import com.courier.domain.Store;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class JsonStoreRepository implements StoreRepository {
    private final List<Store> stores;

    public JsonStoreRepository(ObjectMapper objectMapper, String resourcePath) {
        this(objectMapper, resourcePath, new DefaultStoreIdGenerator());
    }

    public JsonStoreRepository(ObjectMapper objectMapper, String resourcePath, StoreIdGenerator storeIdGenerator) {
        this.stores = loadStores(objectMapper, resourcePath, storeIdGenerator);
    }

    @Override
    public List<Store> findAll() {
        return stores;
    }

    private List<Store> loadStores(ObjectMapper objectMapper, String resourcePath, StoreIdGenerator storeIdGenerator) {
        try (InputStream inputStream = new ClassPathResource(resourcePath).getInputStream()) {
            List<Store> loaded = objectMapper.readValue(inputStream, new TypeReference<List<Store>>() {});
            return normalizeIds(loaded, storeIdGenerator);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load stores from " + resourcePath, ex);
        }
    }

    private List<Store> normalizeIds(List<Store> loaded, StoreIdGenerator storeIdGenerator) {
        return java.util.stream.IntStream.range(0, loaded.size())
                .mapToObj(index -> {
                    Store store = loaded.get(index);
                    String id = storeIdGenerator.generateId(store, index);
                    return new Store(id, store.name(), store.lat(), store.lng());
                })
                .toList();
    }
}
