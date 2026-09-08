package com.sugamflow.observability.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.sugamflow.observability.mdc.MdcKeys;

/**
 * Immutable-style audit stream (separate logger name from technical logs).
 * Persist to an audit store later; this only standardizes the event shape.
 */
public class AuditEventLogger {

    private static final Logger log = LoggerFactory.getLogger("sugamflow.audit");

    public void record(String action, String target, String result) {
        String previous = MDC.get(MdcKeys.EVENT_TYPE);
        try {
            MDC.put(MdcKeys.EVENT_TYPE, "AUDIT");
            MDC.put(MdcKeys.OPERATION, action != null ? action : "UNKNOWN");
            log.info("audit action={} target={} result={}",
                    action != null ? action : "UNKNOWN",
                    target != null ? target : "-",
                    result != null ? result : "UNKNOWN");
        } finally {
            MDC.remove(MdcKeys.OPERATION);
            if (previous == null) {
                MDC.remove(MdcKeys.EVENT_TYPE);
            } else {
                MDC.put(MdcKeys.EVENT_TYPE, previous);
            }
        }
    }
}
