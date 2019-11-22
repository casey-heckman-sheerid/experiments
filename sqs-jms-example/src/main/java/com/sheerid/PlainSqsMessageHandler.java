package com.sheerid;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("WeakerAccess")
@Component
public class PlainSqsMessageHandler implements Runnable {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(PlainSqsMessageHandler.class);

    private final MessageHandlerConfig config;
    private final AmazonSQS client;

    @Autowired
    public PlainSqsMessageHandler(MessageHandlerConfig config) {
        Validate.notNull(config.getRegion(), "region is required");
        Validate.notBlank(config.getQueueUrl(), "queueArn is required");

        this.config = config;

        ClientConfiguration clientConfiguration = new ClientConfiguration().withProtocol(Protocol.HTTPS)
                                                                           .withConnectionTimeout(10000)
                                                                           .withRequestTimeout(30000);
        AmazonSQSClientBuilder clientBuilder = AmazonSQSClientBuilder.standard()
                                                                     .withClientConfiguration(clientConfiguration)
                                                                     .withCredentials(new DefaultAWSCredentialsProviderChain())
                                                                     .withRegion(config.getRegion());
        client = clientBuilder.build();

        LOGGER.info(String.format("Created: config=%s", config));
    }

    public void start() {
        Executors.newScheduledThreadPool(1)
                 .scheduleWithFixedDelay(this, 0, 1, TimeUnit.MILLISECONDS);
    }

    public void run() {
        LOGGER.debug("Receiving messages...");
        ReceiveMessageRequest request = new ReceiveMessageRequest(config.getQueueUrl()).withWaitTimeSeconds(20)
                                                                                       .withMaxNumberOfMessages(1);
        ReceiveMessageResult result = client.receiveMessage(request);
        if (result == null) {
            return;
        }
        List<Message> messages = result.getMessages();
        LOGGER.debug(String.format("Received %s messages", messages.size()));
        for (Message message : messages) {
            onMessage(message);
        }
    }

    private void onMessage(Message message) {
        String messageID;
        try {
            messageID = message.getMessageId();
        } catch (Exception e) {
            messageID = "unknown (error occurred)";
        }

        try {
            String messageText = message.getBody();
            if (StringUtils.isNotBlank(messageText)) {
                OBJECT_MAPPER.readValue(messageText, StructuredMessage.class);
                LOGGER.info(String.format("Received message: %s", messageText));
            }

        } catch (Exception e) {
            LOGGER.error(String.format("Unhandled exception while handling message: messageId=%s", messageID), e);

        } finally {
            try {
                client.deleteMessage(config.getQueueUrl(), message.getReceiptHandle());
            } catch (Exception e) {
                LOGGER.error(String.format("Unhandled exception while acknowledging message: messageId=%s", messageID), e);
            }
        }
    }
}
