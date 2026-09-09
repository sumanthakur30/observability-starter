package com.sugamflow.observability.error;

/** Shared SugamFlow error codes. Domain services may append more specific suffixes. */
public final class ErrorCodes {
    public static final String AUTH = "SF-AUTH-001";
    public static final String SHOP = "SF-SHOP-001";
    public static final String PRODUCT = "SF-PRODUCT-001";
    public static final String ORDER = "SF-ORDER-001";
    public static final String STOCK = "SF-STOCK-001";
    public static final String USER = "SF-USER-001";
    public static final String PAYMENT = "SF-PAYMENT-001";
    public static final String DOCTOR = "SF-DOCTOR-001";
    public static final String APPOINTMENT = "SF-APPOINTMENT-001";
    public static final String QUEUE = "SF-QUEUE-001";
    public static final String IPD = "SF-IPD-001";
    public static final String ACCOMMODATION = "SF-ACCOM-001";
    public static final String LAB = "SF-LAB-001";
    public static final String PHARMACY = "SF-PHARMACY-001";
    public static final String SUBSCRIPTION = "SF-SUBSCRIPTION-001";
    public static final String GATEWAY = "SF-GATEWAY-001";
    public static final String VALIDATION = "SF-VALIDATION-001";
    public static final String NOT_FOUND = "SF-NOT-FOUND-001";
    public static final String FORBIDDEN = "SF-FORBIDDEN-001";
    public static final String CONFLICT = "SF-CONFLICT-001";
    public static final String DATABASE = "SF-DATABASE-001";
    public static final String INTERNAL = "SF-INTERNAL-001";

    private ErrorCodes() {}
}
