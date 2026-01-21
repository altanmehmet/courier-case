package com.courier;

import com.courier.domain.Store;
import com.courier.infra.JsonStoreRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonStoreRepositoryTest {

    @Test
    void loadsStoresAndNormalizesIdsFromTestResource() {
        JsonStoreRepository repo = new JsonStoreRepository(new ObjectMapper(), "test-stores.json");
        List<Store> stores = repo.findAll();

        assertEquals(4, stores.size());

        // 0: blank id -> slugified name
        // Note: Turkish dotless 'ı' is not [a-z] under Locale.ROOT, so it becomes a dash.
        assertEquals("c-g-kofte-123", stores.get(0).id());
        assertEquals("Çığ Köfte 123", stores.get(0).name());

        // 1: missing id + name only punctuation/space -> fallback to store-index
        assertEquals("store-1", stores.get(1).id());

        // 2: explicit id stays
        assertEquals("explicit-id", stores.get(2).id());

        // 3: blank id + null name -> slugify returns "" -> fallback store-index
        assertEquals("store-3", stores.get(3).id());
        assertNotNull(stores.get(3));
    }

    @Test
    void throwsIllegalStateExceptionWhenResourceMissing() {
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> new JsonStoreRepository(new ObjectMapper(), "does-not-exist.json"));
        assertTrue(ex.getMessage().contains("Failed to load stores from does-not-exist.json"));
    }
}

