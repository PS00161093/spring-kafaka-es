package com.ps.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@ComponentScan(basePackages = "com.ps.app")
public class RetryConfig {

    private final RetryConfigData retryConfigData;

    public RetryConfig(RetryConfigData retryConfigData) {
        this.retryConfigData = retryConfigData;
    }

    @Bean
    public RetryTemplate retryTemplate() {
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(retryConfigData.getInitialInterval());
        backOffPolicy.setMaxInterval(retryConfigData.getMaxInterval());
        backOffPolicy.setMultiplier(retryConfigData.getMultiplier());

        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(retryConfigData.getMaxAttempts());

        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.setRetryPolicy(simpleRetryPolicy);

        return retryTemplate;
    }
}
