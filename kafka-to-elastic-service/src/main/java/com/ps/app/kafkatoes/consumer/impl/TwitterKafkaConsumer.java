package com.ps.app.kafkatoes.consumer.impl;

import com.ps.app.client.KafkaAdminClient;
import com.ps.app.configuration.KafkaConfigData;
import com.ps.app.kafkatoes.consumer.KafkaConsumer;
import com.ps.ms.kafka.avro.model.TwitterAvroModel;
import org.apache.avro.AvroTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TwitterKafkaConsumer implements KafkaConsumer<Long, TwitterAvroModel> {

    private static final Logger logger = LoggerFactory.getLogger(TwitterKafkaConsumer.class);

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    private final KafkaAdminClient kafkaAdminClient;

    private final KafkaConfigData kafkaConfigData;

    public TwitterKafkaConsumer(KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry, KafkaAdminClient kafkaAdminClient, KafkaConfigData kafkaConfigData) {
        this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
        this.kafkaAdminClient = kafkaAdminClient;
        this.kafkaConfigData = kafkaConfigData;
    }

    @Override
    @KafkaListener(id = "twitterTopicListener", topics = "${kafka-config.topic-name}")
    public void received(@Payload List<TwitterAvroModel> messages,
                         @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<Integer> keys,
                         @Header(KafkaHeaders.PARTITION_ID) List<Integer> partitions,
                         @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        logger.info("{} number of message received with keys {}, partitions {} and offsets {}, " +
                        "sending it to elastic: Thread id {}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString(),
                Thread.currentThread().getId());

    }
}
