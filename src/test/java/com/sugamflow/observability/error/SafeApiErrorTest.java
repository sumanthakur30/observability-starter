package com.sugamflow.observability.error;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import com.sugamflow.observability.mdc.MdcKeys;

class SafeApiErrorTest {

    @AfterEach
    void clear() {
        MDC.clear();
    }

    @Test
    void includesTraceIdFromMdcNotException() {
        MDC.put(MdcKeys.TRACE_ID, "4bf92f3577b34da6a3ce929d0e0e4736");
        SafeApiError err = SafeApiError.of("INTERNAL_SERVER_ERROR", "Unable to process your request.");
        assertFalse(err.success());
        assertEquals("4bf92f3577b34da6a3ce929d0e0e4736", err.traceId());
        assertEquals("INTERNAL_SERVER_ERROR", err.errorCode());
        assertEquals("INTERNAL_SERVER_ERROR", err.code());
        assertFalse(err.message().contains("Exception"));
        assertTrue(err.referenceId() != null && err.referenceId().startsWith("ERR-"));
        assertNotNull(err.timestamp());
    }
}
