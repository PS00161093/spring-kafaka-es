package com.ps.app.twittertokafka.listener;

import com.ps.app.configuration.KafkaConfigData;
import com.ps.app.service.KafkaProducer;
import com.ps.app.twittertokafka.transformer.TwitterStatusToAvroTransformer;
import com.ps.ms.kafka.avro.model.TwitterAvroModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.StatusAdapter;

@Component
public class TweetsListener extends StatusAdapter {

    private final static Logger logger = LoggerFactory.getLogger(TweetsListener.class);

    private final KafkaConfigData kafkaConfigData;

    private final KafkaProducer<Long, TwitterAvroModel> kafkaProducer;

    private final TwitterStatusToAvroTransformer twitterStatusToAvroTransformer;

    public TweetsListener(KafkaConfigData kafkaConfigData,
                          KafkaProducer<Long, TwitterAvroModel> kafkaProducer,
                          TwitterStatusToAvroTransformer twitterStatusToAvroTransformer) {
        this.kafkaConfigData = kafkaConfigData;
        this.kafkaProducer = kafkaProducer;
        this.twitterStatusToAvroTransformer = twitterStatusToAvroTransformer;
    }

    @Override
    public void onStatus(Status status) {
        logger.info("Received status text: {} sending to Kafka topic {}", status.getText(), kafkaConfigData.getTopicName());
        TwitterAvroModel avroModel = twitterStatusToAvroTransformer.getTwitterAvroModel(status);
        kafkaProducer.send(kafkaConfigData.getTopicName(), avroModel.getUserId(), avroModel);
    }
}
