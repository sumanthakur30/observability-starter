package com.sugamflow.observability.mdc;

public final class MdcKeys {
    public static final String TRACE_ID = "traceId";
    public static final String SPAN_ID = "spanId";
    public static final String PARENT_SPAN_ID = "parentSpanId";
    public static final String REQUEST_ID = "requestId";
    public static final String CORRELATION_ID = "correlationId";
    public static final String TENANT_ID = "tenantId";
    public static final String SHOP_ID = "shopId";
    public static final String BRANCH_ID = "branchId";
    public static final String USER_ID = "userId";
    public static final String ROLE = "role";
    public static final String SERVICE = "service";
    public static final String ENVIRONMENT = "environment";
    public static final String HTTP_METHOD = "httpMethod";
    public static final String HTTP_PATH = "httpPath";
    public static final String STATUS_CODE = "statusCode";
    public static final String DURATION_MS = "durationMs";
    public static final String OPERATION = "operation";
    public static final String EVENT_TYPE = "eventType";

    private MdcKeys() {}
}
