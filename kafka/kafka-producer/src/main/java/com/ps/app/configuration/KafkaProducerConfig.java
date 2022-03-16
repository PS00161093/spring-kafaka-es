package com.ps.app.configuration;

import lombok.Data;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@ComponentScan(basePackages = "com.ps.app")
@ConfigurationProperties(prefix = "kafka-producer-config")
public class KafkaProducerConfig<K extends Serializable, V extends SpecificRecordBase> {

    private final KafkaConfigData kafkaConfigData;

    private final KafkaProducerConfigData kafkaProducerConfigData;

    public KafkaProducerConfig(KafkaConfigData kafkaConfigData, KafkaProducerConfigData kafkaProducerConfigData) {
        this.kafkaConfigData = kafkaConfigData;
        this.kafkaProducerConfigData = kafkaProducerConfigData;
    }

    @Bean
    public Map<String, Object> producerConfig() {
        return Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProducerConfigData.getKeySerializer(),
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProducerConfigData.getValueSerializer(),
                ProducerConfig.BATCH_SIZE_CONFIG, kafkaProducerConfigData.getBatchSize() * kafkaProducerConfigData.getBatchSizeBoostFactor(),
                ProducerConfig.LINGER_MS_CONFIG, kafkaProducerConfigData.getLingerMs(),
                ProducerConfig.COMPRESSION_TYPE_CONFIG, kafkaProducerConfigData.getCompressionType(),
                ProducerConfig.ACKS_CONFIG, kafkaProducerConfigData.getAcks(),
                ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaProducerConfigData.getRequestTimeout(),
                ProducerConfig.RETRIES_CONFIG, kafkaProducerConfigData.getRetryCount(),
                kafkaConfigData.getSchemaRegistryUrlKey(), kafkaConfigData.getSchemaRegistryUrl()
        );
    }

    @Bean
    public ProducerFactory<K, V> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<K, V> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
