package com.ps.app.twittertokafka.runner.impl;

import com.ps.app.configiguration.Config;
import com.ps.app.twittertokafka.listener.TweetsListener;
import com.ps.app.twittertokafka.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import twitter4j.FilterQuery;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.Objects;

@Component
public class TwitterStreamRunnerImpl implements StreamRunner {

    private final static Logger logger = LoggerFactory.getLogger(TwitterStreamRunnerImpl.class);

    private final TweetsListener tweetsListener;

    private TwitterStream twitterStream;

    private final Config config;

    public TwitterStreamRunnerImpl(TweetsListener tweetsListener, Config config) {
        this.tweetsListener = tweetsListener;
        this.config = config;
    }

    @Override
    public void start() {
        twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(tweetsListener);
        addFilter();
    }

    private void addFilter() {
        String[] keywords = config.getTwitterKeywords().toArray(new String[0]);
        FilterQuery filterQuery = new FilterQuery(keywords);
        twitterStream.filter(filterQuery);
        logger.info("Started filtering tweets for keywords {}", Arrays.toString(keywords));
    }

    @PreDestroy
    public void shutdown() {
        if (Objects.nonNull(twitterStream)) {
            logger.info("Closing twitter stream.");
            twitterStream.shutdown();
        }
    }
}
