package com.sugamflow.observability.error;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

class ErrorEnvelopeTest {

    @AfterEach
    void clear() {
        MDC.clear();
    }

    @Test
    void doesNotOverwriteExistingDomainErrorCode() {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Module not enabled");
        body.put("errorCode", "FEATURE_NOT_INCLUDED");
        body.put("code", "MODULE_NOT_AVAILABLE");
        ErrorEnvelope.apply(body, ErrorCodes.SUBSCRIPTION);
        assertEquals("FEATURE_NOT_INCLUDED", body.get("errorCode"));
        assertEquals("MODULE_NOT_AVAILABLE", body.get("code"));
        assertNotNull(body.get("traceId") != null ? body.get("traceId") : body.get("referenceId"));
        assertFalse((Boolean) body.get("success"));
    }
}
