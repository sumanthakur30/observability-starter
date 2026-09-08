package com.sugamflow.observability.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sugamflow.observability.ObservabilityProperties;
import com.sugamflow.observability.mdc.MdcKeys;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Access line only — never request/response bodies. */
public class HttpAccessLogger extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(HttpAccessLogger.class);

    private final ObservabilityProperties properties;

    public HttpAccessLogger(ObservabilityProperties properties) {
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws java.io.IOException, jakarta.servlet.ServletException {
        if (!properties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }
        long started = System.nanoTime();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = (System.nanoTime() - started) / 1_000_000L;
            int status = response.getStatus();
            MDC.put(MdcKeys.STATUS_CODE, String.valueOf(status));
            MDC.put(MdcKeys.DURATION_MS, String.valueOf(durationMs));
            if (status >= 500) {
                log.warn("HTTP {} {} → {} ({}ms)", request.getMethod(), request.getRequestURI(), status, durationMs);
            } else {
                log.info("HTTP {} {} → {} ({}ms)", request.getMethod(), request.getRequestURI(), status, durationMs);
            }
            MDC.remove(MdcKeys.STATUS_CODE);
            MDC.remove(MdcKeys.DURATION_MS);
        }
    }
}
