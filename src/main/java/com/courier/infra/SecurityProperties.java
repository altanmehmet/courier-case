package com.courier.infra;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security")
public class SecurityProperties {
    /**
     * If false, all endpoints are left open (default).
     */
    private boolean enabled = false;

    /**
     * HMAC secret for HS256 when enabled. Provide at least 32 chars in production.
     */
    private String hmacSecret = "change-me-in-prod-32chars-minimum";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getHmacSecret() {
        return hmacSecret;
    }

    public void setHmacSecret(String hmacSecret) {
        this.hmacSecret = hmacSecret;
    }
}

