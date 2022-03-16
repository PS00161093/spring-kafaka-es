package com.ps.app.twittertokafka.init.impl;

import com.ps.app.client.KafkaAdminClient;
import com.ps.app.configuration.KafkaConfigData;
import com.ps.app.twittertokafka.init.StreamInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KafkaStreamInitializer implements StreamInitializer {

    private final static Logger logger = LoggerFactory.getLogger(KafkaStreamInitializer.class);

    private final KafkaConfigData kafkaConfigData;

    private final KafkaAdminClient adminClient;

    public KafkaStreamInitializer(KafkaConfigData kafkaConfigData, KafkaAdminClient adminClient) {
        this.kafkaConfigData = kafkaConfigData;
        this.adminClient = adminClient;
    }

    @Override
    public void init() {
        adminClient.createTopics();
        adminClient.checkSchemaRegistry();
        logger.info("Kafka topics {} are ready operational now!", kafkaConfigData.getTopicNamesToCreate());
    }
}
