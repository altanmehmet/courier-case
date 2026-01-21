package com.courier.infra;

import com.courier.domain.Store;

/**
 * Generates stable store IDs from store data.
 *
 * <p>Important: Implementations must be deterministic and side-effect free.</p>
 */
public interface StoreIdGenerator {
    String generateId(Store store, int index);
}

