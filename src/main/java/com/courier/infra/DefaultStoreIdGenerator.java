package com.courier.infra;

import com.courier.domain.Store;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Default store ID algorithm (kept identical to previous JsonStoreRepository behavior).
 */
public class DefaultStoreIdGenerator implements StoreIdGenerator {
    @Override
    public String generateId(Store store, int index) {
        String id = store.id();
        if (id == null || id.isBlank()) {
            id = slugify(store.name());
        }
        if (id.isBlank()) {
            id = "store-" + index;
        }
        return id;
    }

    private String slugify(String input) {
        if (input == null) {
            return "";
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        String cleaned = normalized.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
        return cleaned.replaceAll("(^-+|-+$)", "");
    }
}

