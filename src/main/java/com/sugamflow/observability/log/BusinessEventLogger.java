package com.sugamflow.observability.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.sugamflow.observability.mdc.MdcKeys;

/** Searchable business events (not audit). No PHI, no payloads. */
public class BusinessEventLogger {

    private static final Logger log = LoggerFactory.getLogger("sugamflow.business");

    public void event(String eventType, String message) {
        event(eventType, null, eventType, null, null, message);
    }

    public void event(
            String eventType,
            String businessModule,
            String businessAction,
            String entityType,
            String entityId,
            String message) {
        if (eventType == null || eventType.isBlank()) {
            return;
        }
        String previousType = MDC.get(MdcKeys.EVENT_TYPE);
        String previousModule = MDC.get(MdcKeys.BUSINESS_MODULE);
        String previousAction = MDC.get(MdcKeys.BUSINESS_ACTION);
        String previousEntityType = MDC.get(MdcKeys.ENTITY_TYPE);
        String previousEntityId = MDC.get(MdcKeys.ENTITY_ID);
        try {
            MDC.put(MdcKeys.EVENT_TYPE, eventType.trim());
            putOrRemove(MdcKeys.BUSINESS_MODULE, businessModule);
            putOrRemove(MdcKeys.BUSINESS_ACTION, businessAction != null ? businessAction : eventType);
            putOrRemove(MdcKeys.ENTITY_TYPE, entityType);
            putOrRemove(MdcKeys.ENTITY_ID, entityId);
            log.info("{}", message != null ? message : eventType);
        } finally {
            restore(MdcKeys.EVENT_TYPE, previousType);
            restore(MdcKeys.BUSINESS_MODULE, previousModule);
            restore(MdcKeys.BUSINESS_ACTION, previousAction);
            restore(MdcKeys.ENTITY_TYPE, previousEntityType);
            restore(MdcKeys.ENTITY_ID, previousEntityId);
        }
    }

    private static void putOrRemove(String key, String value) {
        if (value != null && !value.isBlank()) {
            MDC.put(key, value.trim());
        } else {
            MDC.remove(key);
        }
    }

    private static void restore(String key, String previous) {
        if (previous == null) {
            MDC.remove(key);
        } else {
            MDC.put(key, previous);
        }
    }
}
