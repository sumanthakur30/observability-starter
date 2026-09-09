package com.sugamflow.observability.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.sugamflow.observability.mdc.MdcKeys;

/** Downstream / SaaS integration timing. Never log request bodies or secrets. */
public class ExternalCallLogger {

    private static final Logger log = LoggerFactory.getLogger("sugamflow.external");

    public void record(
            String serviceName,
            String operation,
            String target,
            long durationMs,
            boolean success,
            int retryCount,
            String errorCode) {
        String previous = MDC.get(MdcKeys.OPERATION);
        try {
            if (operation != null && !operation.isBlank()) {
                MDC.put(MdcKeys.OPERATION, operation.trim());
            }
            MDC.put(MdcKeys.DURATION_MS, Long.toString(Math.max(0, durationMs)));
            if (success) {
                log.info(
                        "external service={} operation={} target={} durationMs={} retries={} status=OK",
                        nz(serviceName),
                        nz(operation),
                        nz(target),
                        durationMs,
                        retryCount);
            } else {
                log.warn(
                        "external service={} operation={} target={} durationMs={} retries={} status=FAIL errorCode={}",
                        nz(serviceName),
                        nz(operation),
                        nz(target),
                        durationMs,
                        retryCount,
                        nz(errorCode));
            }
        } finally {
            MDC.remove(MdcKeys.DURATION_MS);
            if (previous == null) {
                MDC.remove(MdcKeys.OPERATION);
            } else {
                MDC.put(MdcKeys.OPERATION, previous);
            }
        }
    }

    private static String nz(String value) {
        return value != null && !value.isBlank() ? value : "-";
    }
}
