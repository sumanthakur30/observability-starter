package com.sugamflow.observability.error;

import java.time.Instant;

import org.slf4j.MDC;

import com.sugamflow.observability.mdc.MdcKeys;

/** Safe client error — never include stack traces or internals. */
public record SafeApiError(
        boolean success,
        String errorCode,
        String message,
        String referenceId,
        String traceId,
        String timestamp) {

    public static SafeApiError of(String code, String message) {
        String trace = firstNonBlank(MDC.get(MdcKeys.TRACE_ID), MDC.get(MdcKeys.REQUEST_ID));
        String ref = ReferenceIds.currentOrCreate();
        String resolvedCode = code != null && !code.isBlank() ? code : ErrorCodes.INTERNAL;
        MDC.put(MdcKeys.ERROR_CODE, resolvedCode);
        return new SafeApiError(
                false,
                resolvedCode,
                message != null && !message.isBlank() ? message : "Unable to process your request.",
                ref,
                trace,
                Instant.now().toString());
    }

    /** Backward-compatible alias used by earlier callers. */
    public String code() {
        return errorCode;
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
