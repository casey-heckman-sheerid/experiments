package com.sheerid;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.fasterxml.jackson.core.JsonProcessingException;

@SuppressWarnings("WeakerAccess")
@Component
public class MessageProducer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProducer.class);

    private final MessageProducerConfig config;
    private final Scanner inputScanner;
    private final AmazonSNS snsClient;

    @Autowired
    public MessageProducer(MessageProducerConfig config) {
        Validate.notNull(config.getRegion(), "region is required");
        Validate.notBlank(config.getTopicArn(), "topicArn is required");

        this.config = config;
        this.inputScanner = new Scanner(System.in);

        this.snsClient = AmazonSNSClientBuilder.standard()
                                               .withRegion(config.getRegion())
                                               .build();

        LOGGER.info(String.format("Created: config=%s", config));
    }

    public void start() {
        Executors.newScheduledThreadPool(1)
                 .scheduleWithFixedDelay(this, 1, 4, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        System.out.println("Press Enter to send message...");
        System.out.flush();
        try {
            inputScanner.nextLine();
        } catch (NoSuchElementException | IllegalStateException e) {
            LOGGER.trace("Interrupted");
            return;
        }

        StructuredMessage message = createMessage();
        String jsonMessage = null;
        try {
            jsonMessage = JsonUtils.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error serializing message", e);
            return;
        }

        try {
            LOGGER.info(String.format("Sending message: %s", jsonMessage));
            snsClient.publish(new PublishRequest().withTopicArn(config.getTopicArn())
                                                  .withMessage(jsonMessage));
        } catch (Exception e) {
            LOGGER.error(String.format("Error sending message: id=%s", message.getId()), e);
        }
    }

    private StructuredMessage createMessage() {
        return new StructuredMessage(UUID.randomUUID()
                                         .toString(), new Date());
    }
}
