package com.sugamflow.observability;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sugam.observability")
public class ObservabilityProperties {

    /** Master switch. When false, filters still no-op and OTLP export stays off. */
    private boolean enabled = true;

    private boolean tenantContextEnabled = true;

    /** JSON stdout (production). Console pattern remains for local unless forced. */
    private boolean jsonLogs = false;

    private long slowQueryWarnMs = 1000;

    private long slowQueryErrorMs = 3000;

    /** Never log HTTP bodies unless this is true AND profile is not production. */
    private boolean payloadLogging = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isTenantContextEnabled() {
        return tenantContextEnabled;
    }

    public void setTenantContextEnabled(boolean tenantContextEnabled) {
        this.tenantContextEnabled = tenantContextEnabled;
    }

    public boolean isJsonLogs() {
        return jsonLogs;
    }

    public void setJsonLogs(boolean jsonLogs) {
        this.jsonLogs = jsonLogs;
    }

    public long getSlowQueryWarnMs() {
        return slowQueryWarnMs;
    }

    public void setSlowQueryWarnMs(long slowQueryWarnMs) {
        this.slowQueryWarnMs = slowQueryWarnMs;
    }

    public long getSlowQueryErrorMs() {
        return slowQueryErrorMs;
    }

    public void setSlowQueryErrorMs(long slowQueryErrorMs) {
        this.slowQueryErrorMs = slowQueryErrorMs;
    }

    public boolean isPayloadLogging() {
        return payloadLogging;
    }

    public void setPayloadLogging(boolean payloadLogging) {
        this.payloadLogging = payloadLogging;
    }
}
