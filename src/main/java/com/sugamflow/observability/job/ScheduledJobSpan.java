package com.sugamflow.observability.job;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.sugamflow.observability.mdc.MdcKeys;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;

/** Wrap scheduled work so each run has a span and executionId. */
public class ScheduledJobSpan {

    private static final Logger log = LoggerFactory.getLogger(ScheduledJobSpan.class);

    private final Tracer tracer;

    public ScheduledJobSpan(Tracer tracer) {
        this.tracer = tracer;
    }

    public void run(String jobName, Runnable work) {
        String executionId = UUID.randomUUID().toString();
        Span span = tracer != null ? tracer.nextSpan().name("job." + jobName).start() : null;
        Tracer.SpanInScope scope = span != null ? tracer.withSpan(span) : null;
        String prevOp = MDC.get(MdcKeys.OPERATION);
        long started = System.nanoTime();
        try {
            MDC.put(MdcKeys.OPERATION, jobName);
            MDC.put("executionId", executionId);
            work.run();
            long ms = (System.nanoTime() - started) / 1_000_000L;
            log.info("JOB={} executionId={} durationMs={} result=SUCCESS", jobName, executionId, ms);
        } catch (RuntimeException ex) {
            long ms = (System.nanoTime() - started) / 1_000_000L;
            log.error("JOB={} executionId={} durationMs={} result=FAILED errorType={}",
                    jobName, executionId, ms, ex.getClass().getSimpleName());
            throw ex;
        } finally {
            if (prevOp == null) {
                MDC.remove(MdcKeys.OPERATION);
            } else {
                MDC.put(MdcKeys.OPERATION, prevOp);
            }
            MDC.remove("executionId");
            if (scope != null) {
                scope.close();
            }
            if (span != null) {
                span.end();
            }
        }
    }
}
