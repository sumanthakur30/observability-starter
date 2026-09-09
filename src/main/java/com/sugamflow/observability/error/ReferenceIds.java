package com.sugamflow.observability.error;

import java.util.UUID;

import org.slf4j.MDC;

import com.sugamflow.observability.mdc.MdcKeys;

/** Short support reference (ERR-XXXXXX). Same value for the request when already on MDC. */
public final class ReferenceIds {

    private ReferenceIds() {}

    public static String currentOrCreate() {
        String existing = MDC.get(MdcKeys.REFERENCE_ID);
        if (existing != null && !existing.isBlank()) {
            return existing.trim();
        }
        String generated = "ERR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        MDC.put(MdcKeys.REFERENCE_ID, generated);
        return generated;
    }
}
