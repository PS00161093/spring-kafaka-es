package com.ps.app.configiguration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "twitter-to-kafka-service")
@Data
public class Config {
    private List<String> twitterKeywords;

    private String welcomeMessage;
}
