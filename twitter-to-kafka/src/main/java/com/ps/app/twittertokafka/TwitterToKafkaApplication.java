package com.ps.app.twittertokafka;

import com.ps.app.twittertokafka.init.StreamInitializer;
import com.ps.app.twittertokafka.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import twitter4j.TwitterException;

@SpringBootApplication
@ComponentScan(basePackages = "com.ps.app")
public class TwitterToKafkaApplication implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(TwitterToKafkaApplication.class);

    private final StreamRunner streamRunner;

    private final StreamInitializer streamInitializer;

    public TwitterToKafkaApplication(StreamRunner streamRunner, StreamInitializer streamInitializer) {
        this.streamRunner = streamRunner;
        this.streamInitializer = streamInitializer;
    }

    public static void main(String[] args) {
        SpringApplication.run(TwitterToKafkaApplication.class, args);
    }

    @Override
    public void run(String... args) throws TwitterException {
        logger.info("Twitter to Kafka app starts!");
        streamInitializer.init();
        streamRunner.start();
    }

}
