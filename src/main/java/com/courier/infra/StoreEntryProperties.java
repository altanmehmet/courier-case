package com.courier.infra;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Externalized settings for store entry rules.
 *
 * <p>Defaults are identical to the original hard-coded values.</p>
 */
@ConfigurationProperties(prefix = "store.entry")
public class StoreEntryProperties {
    private double radiusMeters = 100.0;
    private long cooldownMillis = 60_000L;

    public double getRadiusMeters() {
        return radiusMeters;
    }

    public void setRadiusMeters(double radiusMeters) {
        this.radiusMeters = radiusMeters;
    }

    public long getCooldownMillis() {
        return cooldownMillis;
    }

    public void setCooldownMillis(long cooldownMillis) {
        this.cooldownMillis = cooldownMillis;
    }
}

