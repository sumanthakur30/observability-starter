package com.sugamflow.observability.error;

import java.util.Map;

import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;

import com.sugamflow.observability.mdc.MdcKeys;

/** Attach correlation fields to existing service error bodies without replacing them. */
public final class ErrorEnvelope {

    public static final String TRACE_HEADER = "X-Trace-Id";
    public static final String ERROR_REF_HEADER = "X-Error-Ref";

    private ErrorEnvelope() {}

    public static SafeApiError safe(String errorCode, String message) {
        return SafeApiError.of(errorCode, message);
    }

    public static void apply(Map<String, Object> body, String errorCode) {
        if (body == null) {
            return;
        }
        Object rawMessage = body.get("message");
        SafeApiError error = SafeApiError.of(errorCode, rawMessage != null ? rawMessage.toString() : null);
        body.putIfAbsent("success", false);
        if (!body.containsKey("errorCode") && !body.containsKey("code")) {
            body.put("errorCode", error.errorCode());
        }
        body.put("referenceId", error.referenceId());
        body.put("traceId", error.traceId());
    }

    public static HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        String trace = firstNonBlank(MDC.get(MdcKeys.TRACE_ID), MDC.get(MdcKeys.REQUEST_ID));
        if (trace != null) {
            headers.set(TRACE_HEADER, trace);
        }
        headers.set(ERROR_REF_HEADER, ReferenceIds.currentOrCreate());
        return headers;
    }

    private static String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) {
            return a.trim();
        }
        if (b != null && !b.isBlank()) {
            return b.trim();
        }
        return null;
    }
}
