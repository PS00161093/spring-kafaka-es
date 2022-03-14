package com.ps.app.twittertokafka.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.StatusAdapter;

@Component
public class TweetsListener extends StatusAdapter {

    private final static Logger logger = LoggerFactory.getLogger(TweetsListener.class);

    @Override
    public void onStatus(Status status) {
        logger.info("Tweet: {}", status.getText());
    }
}
