package com.sugamflow.observability.jdbc;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SlowQueryLoggerTest {

    @Test
    void truncatesAndFlattensSql() {
        String sql = "select   *\nfrom patients where id = ?";
        String safe = SlowQueryLogger.sanitize(sql);
        assertTrue(safe.contains("select * from patients"));
        assertTrue(safe.length() < 100);
    }
}
