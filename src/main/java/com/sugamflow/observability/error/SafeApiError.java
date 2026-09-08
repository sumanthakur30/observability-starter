package com.sugamflow.observability.error;

import java.time.Instant;

import org.slf4j.MDC;

import com.sugamflow.observability.mdc.MdcKeys;

/** Safe client error — never include stack traces or internals. */
public record SafeApiError(
        boolean success,
        String code,
        String message,
        String traceId,
        String timestamp) {

    public static SafeApiError of(String code, String message) {
        String trace = firstNonBlank(MDC.get(MdcKeys.TRACE_ID), MDC.get(MdcKeys.REQUEST_ID));
        return new SafeApiError(
                false,
                code != null ? code : "INTERNAL_SERVER_ERROR",
                message != null ? message : "Unable to process your request.",
                trace,
                Instant.now().toString());
    }

    private static String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) {
            return a;
        }
        if (b != null && !b.isBlank()) {
            return b;
        }
        return null;
    }
}
