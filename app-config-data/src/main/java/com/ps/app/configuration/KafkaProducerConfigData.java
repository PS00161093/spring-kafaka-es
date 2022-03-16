package com.ps.app.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-producer-config")
public class KafkaProducerConfigData {

    private String acks;

    private Integer lingerMs;

    private Integer batchSize;

    private Integer retryCount;

    private String keySerializer;

    private Integer requestTimeout;

    private String valueSerializer;

    private String compressionType;

    private Integer batchSizeBoostFactor;
}
