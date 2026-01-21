package com.courier;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CourierTrackingApplicationMainTest {

    @Test
    void mainStartsAndExitsWhenExitFlagEnabled() {
        assertDoesNotThrow(() -> CourierTrackingApplication.main(new String[]{
                "--spring.main.web-application-type=none",
                "--app.exitAfterStartup=true"
        }));
    }
}

