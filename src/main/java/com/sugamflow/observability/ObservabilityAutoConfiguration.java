package com.sugamflow.observability;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

import com.sugamflow.observability.jdbc.SlowQueryLogger;
import com.sugamflow.observability.job.ScheduledJobSpan;
import com.sugamflow.observability.log.AuditEventLogger;
import com.sugamflow.observability.log.BusinessEventLogger;
import com.sugamflow.observability.log.HttpAccessLogger;
import com.sugamflow.observability.mdc.ObservabilityMdcFilter;

import io.micrometer.tracing.Tracer;
import jakarta.servlet.Filter;

@AutoConfiguration
@EnableConfigurationProperties(ObservabilityProperties.class)
@ConditionalOnProperty(prefix = "sugam.observability", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ObservabilityAutoConfiguration {

    @Bean
    public BusinessEventLogger businessEventLogger() {
        return new BusinessEventLogger();
    }

    @Bean
    public AuditEventLogger auditEventLogger() {
        return new AuditEventLogger();
    }

    @Bean
    public SlowQueryLogger slowQueryLogger(ObservabilityProperties properties) {
        return new SlowQueryLogger(properties);
    }

    @Bean
    public ScheduledJobSpan scheduledJobSpan(ObjectProvider<Tracer> tracer) {
        return new ScheduledJobSpan(tracer.getIfAvailable());
    }

    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnClass(Filter.class)
    public FilterRegistrationBean<ObservabilityMdcFilter> observabilityMdcFilter(
            ObservabilityProperties properties,
            ObjectProvider<Tracer> tracer,
            @Value("${spring.application.name:unknown-service}") String serviceName,
            @Value("${SUGAM_ENVIRONMENT:${spring.profiles.active:local}}") String environment) {
        FilterRegistrationBean<ObservabilityMdcFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new ObservabilityMdcFilter(properties, tracer.getIfAvailable(), serviceName, environment));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 20);
        bean.addUrlPatterns("/*");
        return bean;
    }

    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnClass(Filter.class)
    public FilterRegistrationBean<HttpAccessLogger> httpAccessLogger(ObservabilityProperties properties) {
        FilterRegistrationBean<HttpAccessLogger> bean = new FilterRegistrationBean<>();
        bean.setFilter(new HttpAccessLogger(properties));
        bean.setOrder(Ordered.LOWEST_PRECEDENCE - 10);
        bean.addUrlPatterns("/*");
        return bean;
    }
}
