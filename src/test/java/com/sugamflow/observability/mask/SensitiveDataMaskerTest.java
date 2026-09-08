package com.sugamflow.observability.mask;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SensitiveDataMaskerTest {

    @Test
    void masksPasswordKeys() {
        assertTrue(SensitiveDataMasker.isSensitiveKey("password"));
        assertTrue(SensitiveDataMasker.isSensitiveKey("refresh_token"));
        assertTrue(SensitiveDataMasker.isSensitiveKey("Authorization"));
        assertFalse(SensitiveDataMasker.isSensitiveKey("shopId"));
        assertEquals("******", SensitiveDataMasker.maskValue("password", "Secret123"));
        assertEquals("POLY-01", SensitiveDataMasker.maskValue("shopId", "POLY-01"));
    }

    @Test
    void redactsJsonAndBearer() {
        String raw = "{\"password\":\"hunter2\",\"shopId\":\"POLY-01\"} Authorization: Bearer abc.def.ghi";
        String redacted = SensitiveDataMasker.redact(raw);
        assertTrue(redacted.contains("******"));
        assertFalse(redacted.contains("hunter2"));
        assertFalse(redacted.contains("abc.def.ghi"));
        assertTrue(redacted.contains("POLY-01"));
    }
}
