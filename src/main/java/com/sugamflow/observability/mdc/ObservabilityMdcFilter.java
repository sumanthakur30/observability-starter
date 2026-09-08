package com.sugamflow.observability.mdc;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sugamflow.observability.ObservabilityProperties;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Complements existing RequestIdFilter. Does not replace tenant/JWT security.
 * Tenant/user values are copied from gateway-verified headers only.
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class ObservabilityMdcFilter extends OncePerRequestFilter {

    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String CORRELATION_HEADER = "X-Correlation-Id";
    public static final String TRACE_RESPONSE_HEADER = "X-Trace-Id";

    private final ObservabilityProperties properties;
    private final Tracer tracer;
    private final String serviceName;
    private final String environment;

    public ObservabilityMdcFilter(
            ObservabilityProperties properties,
            Tracer tracer,
            String serviceName,
            String environment) {
        this.properties = properties;
        this.tracer = tracer;
        this.serviceName = serviceName;
        this.environment = environment;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!properties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestId = firstNonBlank(
                request.getHeader(REQUEST_ID_HEADER),
                request.getHeader(CORRELATION_HEADER),
                MDC.get(MdcKeys.REQUEST_ID));
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }

        put(MdcKeys.REQUEST_ID, requestId);
        put(MdcKeys.CORRELATION_ID, firstNonBlank(request.getHeader(CORRELATION_HEADER), requestId));
        put(MdcKeys.SERVICE, serviceName);
        put(MdcKeys.ENVIRONMENT, environment);
        put(MdcKeys.HTTP_METHOD, request.getMethod());
        put(MdcKeys.HTTP_PATH, request.getRequestURI());

        if (properties.isTenantContextEnabled()) {
            put(MdcKeys.TENANT_ID, request.getHeader("X-Tenant-Id"));
            put(MdcKeys.SHOP_ID, request.getHeader("X-Shop-Id"));
            put(MdcKeys.BRANCH_ID, request.getHeader("X-Branch-Id"));
            put(MdcKeys.USER_ID, firstNonBlank(request.getHeader("X-Auth-User"), request.getHeader("X-User-Id")));
            put(MdcKeys.ROLE, firstNonBlank(request.getHeader("X-Auth-Role"), request.getHeader("X-Role-Code")));
        }

        applyTrace(response);

        response.setHeader(REQUEST_ID_HEADER, requestId);
        response.setHeader(CORRELATION_HEADER, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    private void applyTrace(HttpServletResponse response) {
        if (tracer == null) {
            return;
        }
        Span span = tracer.currentSpan();
        if (span == null || span.context() == null) {
            return;
        }
        String traceId = span.context().traceId();
        String spanId = span.context().spanId();
        put(MdcKeys.TRACE_ID, traceId);
        put(MdcKeys.SPAN_ID, spanId);
        if (traceId != null && !traceId.isBlank()) {
            response.setHeader(TRACE_RESPONSE_HEADER, traceId);
        }
    }

    private static void put(String key, String value) {
        if (value != null && !value.isBlank()) {
            MDC.put(key, value.trim());
        }
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }
}
