package com.courier.application;

/**
 * Immutable config for store entry rules.
 *
 * <p>Defaults match the original behavior.</p>
 */
public record StoreEntryConfig(double entryRadiusMeters, long reentryCooldownMillis) {
    public static StoreEntryConfig defaults() {
        return new StoreEntryConfig(100.0, 60_000L);
    }
}

