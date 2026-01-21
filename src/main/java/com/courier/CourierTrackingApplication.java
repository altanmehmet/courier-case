package com.courier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
public class CourierTrackingApplication {
    public static void main(String[] args) {
        SpringApplication.run(CourierTrackingApplication.class, args);
    }

    /**
     * Test-support hook: when {@code app.exitAfterStartup=true}, the application context is closed on startup.
     * This is intentionally opt-in so normal runs are unaffected.
     */
    @Component
    static class ExitAfterStartup implements ApplicationListener<ApplicationReadyEvent> {
        private final boolean exitAfterStartup;

        ExitAfterStartup(@Value("${app.exitAfterStartup:false}") boolean exitAfterStartup) {
            this.exitAfterStartup = exitAfterStartup;
        }

        @Override
        public void onApplicationEvent(ApplicationReadyEvent event) {
            if (exitAfterStartup) {
                event.getApplicationContext().close();
            }
        }
    }
}
