package com.ps.app.client;

import com.ps.app.configiguration.KafkaConfigData;
import com.ps.app.configiguration.RetryConfigData;
import com.ps.app.exception.KafkaClientException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
public class KafkaAdminClient {

    private static final Logger logger = LoggerFactory.getLogger(KafkaAdminClient.class);

    private final AdminClient adminClient;

    private final RetryTemplate retryTemplate;

    private final KafkaConfigData kafkaConfigData;

    private final RetryConfigData retryConfigData;

    private final WebClient webClient;

    public KafkaAdminClient(KafkaConfigData kafkaConfigData,
                            RetryConfigData retryConfigData,
                            AdminClient adminClient,
                            RetryTemplate retryTemplate,
                            WebClient webClient) {
        this.kafkaConfigData = kafkaConfigData;
        this.retryConfigData = retryConfigData;
        this.adminClient = adminClient;
        this.retryTemplate = retryTemplate;
        this.webClient = webClient;
    }

    public void createTopics() {
        CreateTopicsResult createTopicsResult;
        try {
            createTopicsResult = retryTemplate.execute(retryContext -> doCreateTopics(retryContext));
        } catch (Throwable t) {
            throw new KafkaClientException("Reached max retries for Kafka topic(s) creation!", t);
        }
        checkTopicsCreated();
    }

    private CreateTopicsResult doCreateTopics(RetryContext retryContext) {
        List<String> topicsToCreate = kafkaConfigData.getTopicNamesToCreate();
        logger.info("Creating Kafka topics :: {}, attempt :: {}", topicsToCreate, retryContext.getRetryCount());
        List<NewTopic> topics = topicsToCreate.stream()
                .map(topic -> new NewTopic(
                        topic.trim(),
                        kafkaConfigData.getNumberOfPartitions(),
                        kafkaConfigData.getReplicationFactor()
                ))
                .collect(Collectors.toList());

        return adminClient.createTopics(topics);
    }

    private Collection<TopicListing> getTopics() {
        Collection<TopicListing> topicListings;
        try {
            topicListings = retryTemplate.execute(retryContext -> doGetTopicListings(retryContext));
        } catch (Throwable t) {
            throw new KafkaClientException("Reached max retries for reading Kafka topic(s)!", t);
        }

        return topicListings;
    }

    private Collection<TopicListing> doGetTopicListings(RetryContext retryContext) throws ExecutionException, InterruptedException {
        logger.info("Reading kafka topics :: {}, attempt :: {}", kafkaConfigData.getTopicNamesToCreate().toArray(), retryContext.getRetryCount());
        Collection<TopicListing> topicListings = adminClient.listTopics().listings().get();
        if (!topicListings.isEmpty()) topicListings.forEach(topicListing -> logger.info(topicListing.name()));

        return topicListings;
    }

    public void checkTopicsCreated() {
        Collection<TopicListing> topicListings = getTopics();
        int retryCount = 1;
        Integer maxRetry = retryConfigData.getMaxAttempts();
        int multiplier = retryConfigData.getMultiplier().intValue();
        Long sleepTime = retryConfigData.getSleepTime();

        for (String topic : kafkaConfigData.getTopicNamesToCreate()) {
            while (!isTopicCreated(topicListings, topic)) {
                checkMaxRetry(retryCount++, maxRetry);
                sleep(sleepTime);
                sleepTime *= multiplier;
                topicListings = getTopics();
            }
        }
    }

    private void sleep(Long sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkMaxRetry(int retryCount, Integer maxRetryCount) {
        if (retryCount > maxRetryCount)
            throw new KafkaClientException("Reached max number of retry attempts for reading topics!");
    }

    private boolean isTopicCreated(Collection<TopicListing> topicListings, String topic) {
        if (Objects.isNull(topicListings)) return false;
        return topicListings.stream()
                .anyMatch(topicListing -> topicListing.name().equalsIgnoreCase(topic));
    }

    public void checkSchemaRegistry() {
        int retryCount = 1;
        Integer maxRetry = retryConfigData.getMaxAttempts();
        int multiplier = retryConfigData.getMultiplier().intValue();
        Long sleepTime = retryConfigData.getSleepTime();

        while (!getSchemaRegistryStatus().is2xxSuccessful()) {
            checkMaxRetry(retryCount++, maxRetry);
            sleep(sleepTime);
            sleepTime *= multiplier;
        }
    }

    private HttpStatus getSchemaRegistryStatus() {
        try {
            return webClient
                    .method(HttpMethod.GET)
                    .uri(kafkaConfigData.getSchemaRegistryUrl())
                    .exchange()
                    .map(clientResponse -> clientResponse.statusCode())
                    .block();
        } catch (Exception ex) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
    }
}
