package com.courier;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ExitAfterStartupTest {

    @Test
    void closesContextWhenEnabled() {
        ConfigurableApplicationContext ctx = Mockito.mock(ConfigurableApplicationContext.class);
        ApplicationReadyEvent event = new ApplicationReadyEvent(
                new SpringApplication(CourierTrackingApplication.class),
                new String[0],
                ctx,
                Duration.ZERO
        );

        CourierTrackingApplication.ExitAfterStartup listener = new CourierTrackingApplication.ExitAfterStartup(true);
        listener.onApplicationEvent(event);

        verify(ctx).close();
    }

    @Test
    void doesNotCloseContextWhenDisabled() {
        ConfigurableApplicationContext ctx = Mockito.mock(ConfigurableApplicationContext.class);
        ApplicationReadyEvent event = new ApplicationReadyEvent(
                new SpringApplication(CourierTrackingApplication.class),
                new String[0],
                ctx,
                Duration.ZERO
        );

        CourierTrackingApplication.ExitAfterStartup listener = new CourierTrackingApplication.ExitAfterStartup(false);
        assertDoesNotThrow(() -> listener.onApplicationEvent(event));

        verify(ctx, never()).close();
    }
}

