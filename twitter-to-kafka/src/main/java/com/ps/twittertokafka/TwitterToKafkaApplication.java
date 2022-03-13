package com.ps.twittertokafka;

import com.ps.twittertokafka.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class TwitterToKafkaApplication implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(TwitterToKafkaApplication.class);

    private final Config config;

    public TwitterToKafkaApplication(Config config) {
        this.config = config;
    }

    public static void main(String[] args) {
        SpringApplication.run(TwitterToKafkaApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("Twitter to Kafka app starts!");
        logger.info(Arrays.toString(config.getTwitterKeywords().toArray(new String[]{})));
    }
}
