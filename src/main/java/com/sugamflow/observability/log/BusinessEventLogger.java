package com.sugamflow.observability.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.sugamflow.observability.mdc.MdcKeys;

/** Searchable business events (not audit). No PHI, no payloads. */
public class BusinessEventLogger {

    private static final Logger log = LoggerFactory.getLogger("sugamflow.business");

    public void event(String eventType, String message) {
        if (eventType == null || eventType.isBlank()) {
            return;
        }
        String previous = MDC.get(MdcKeys.EVENT_TYPE);
        try {
            MDC.put(MdcKeys.EVENT_TYPE, eventType.trim());
            log.info("{}", message != null ? message : eventType);
        } finally {
            if (previous == null) {
                MDC.remove(MdcKeys.EVENT_TYPE);
            } else {
                MDC.put(MdcKeys.EVENT_TYPE, previous);
            }
        }
    }
}
