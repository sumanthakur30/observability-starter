package com.sugamflow.observability.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sugamflow.observability.ObservabilityProperties;

/** Hibernate-style slow query hook. SQL text is truncated; parameters are never logged. */
public class SlowQueryLogger {

    private static final Logger log = LoggerFactory.getLogger(SlowQueryLogger.class);

    private final ObservabilityProperties properties;

    public SlowQueryLogger(ObservabilityProperties properties) {
        this.properties = properties;
    }

    public void record(String sql, long durationMs) {
        if (!properties.isEnabled() || sql == null) {
            return;
        }
        String safe = sanitize(sql);
        if (durationMs >= properties.getSlowQueryErrorMs()) {
            log.error("Slow query {}ms sql={}", durationMs, safe);
        } else if (durationMs >= properties.getSlowQueryWarnMs()) {
            log.warn("Slow query {}ms sql={}", durationMs, safe);
        }
    }

    static String sanitize(String sql) {
        String oneLine = sql.replaceAll("\\s+", " ").trim();
        if (oneLine.length() > 400) {
            return oneLine.substring(0, 400) + "…";
        }
        return oneLine;
    }
}
