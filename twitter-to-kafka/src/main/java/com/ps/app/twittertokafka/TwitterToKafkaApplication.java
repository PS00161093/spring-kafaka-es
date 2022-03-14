package com.ps.app.twittertokafka;

import com.ps.app.configiguration.Config;
import com.ps.app.twittertokafka.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import twitter4j.TwitterException;

import java.util.Arrays;

@SpringBootApplication
@ComponentScan(basePackages = "com.ps.app")
public class TwitterToKafkaApplication implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(TwitterToKafkaApplication.class);

    private final StreamRunner streamRunner;

    private final Config config;

    public TwitterToKafkaApplication(StreamRunner streamRunner, Config config) {
        this.streamRunner = streamRunner;
        this.config = config;
    }

    public static void main(String[] args) {
        SpringApplication.run(TwitterToKafkaApplication.class, args);
    }

    @Override
    public void run(String... args) throws TwitterException {
        logger.info("Twitter to Kafka app starts!");
        logger.info(config.getWelcomeMessage());
        logger.info(Arrays.toString(config.getTwitterKeywords().toArray(new String[]{})));
        streamRunner.start();
    }

}