package com.courier;

import com.courier.infra.StoreEntryProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StoreEntryPropertiesTest {
    @Test
    void defaultsMatchOriginalConstants() {
        StoreEntryProperties props = new StoreEntryProperties();
        assertEquals(100.0, props.getRadiusMeters(), 0.0);
        assertEquals(60_000L, props.getCooldownMillis());
    }

    @Test
    void settersAndGettersWork() {
        StoreEntryProperties props = new StoreEntryProperties();
        props.setRadiusMeters(123.45);
        props.setCooldownMillis(9876L);

        assertEquals(123.45, props.getRadiusMeters(), 0.0);
        assertEquals(9876L, props.getCooldownMillis());
    }
}

