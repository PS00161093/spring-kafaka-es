package com.ps.app.service.impl;

import com.ps.app.service.KafkaProducer;
import com.ps.ms.kafka.avro.model.TwitterAvroModel;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.PreDestroy;
import java.util.Objects;

@Service
public class TwitterKafkaProducer implements KafkaProducer<Long, TwitterAvroModel> {

    private static final Logger logger = LoggerFactory.getLogger(TwitterKafkaProducer.class);

    private KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate;

    public TwitterKafkaProducer(KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(String topic, Long key, TwitterAvroModel msg) {
        logger.info("Sending message :: {} {}", key, msg);
        ListenableFuture<SendResult<Long, TwitterAvroModel>> sentRecord = kafkaTemplate.send(topic, key, msg);
        addCallBack(topic, msg, sentRecord);
    }

    private void addCallBack(String topic, TwitterAvroModel msg, ListenableFuture<SendResult<Long, TwitterAvroModel>> sentRecord) {
        sentRecord.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                logger.error("Error while sending message {} to topic {}", msg.toString(), topic.toString());
            }

            @Override
            public void onSuccess(SendResult<Long, TwitterAvroModel> result) {
                RecordMetadata recordMetadata = result.getRecordMetadata();
                logger.debug("Received new metadata. Topic :: {}; Partition :: {}; OffSet :: {}; TimeStamp :: {};",
                        recordMetadata.topic(),
                        recordMetadata.partition(),
                        recordMetadata.offset(),
                        recordMetadata.timestamp());
            }
        });
    }

    @PreDestroy
    public void close() {
        if (Objects.nonNull(kafkaTemplate)) {
            logger.info("Closing Kafka producer!");
            kafkaTemplate.destroy();
        }
    }

}
