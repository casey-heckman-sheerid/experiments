package com.sheerid;

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("WeakerAccess")
@Component
public class JmsSqsMessageHandler implements MessageListener {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsSqsMessageHandler.class);

    private final MessageHandlerConfig config;

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private SQSConnection startedConnection;

    @Autowired
    public JmsSqsMessageHandler(MessageHandlerConfig config) {
        Validate.notNull(config.getRegion(), "region is required");
        Validate.notBlank(config.getQueueUrl(), "queueArn is required");

        this.config = config;
        LOGGER.info(String.format("Created: config=%s", config));
    }

    public void start() {
        String queueName = StringUtils.substringAfterLast(config.getQueueUrl(), "");
        SQSConnection connection = null;
        try {
            ClientConfiguration clientConfiguration = new ClientConfiguration().withProtocol(Protocol.HTTPS)
                                                                               .withConnectionTimeout(10000)
                                                                               .withRequestTimeout(30000);
            AmazonSQSClientBuilder clientBuilder = AmazonSQSClientBuilder.standard()
                                                                         .withClientConfiguration(clientConfiguration)
                                                                         .withCredentials(new DefaultAWSCredentialsProviderChain())
                                                                         .withRegion(config.getRegion());

            connection = new SQSConnectionFactory(new ProviderConfiguration(), clientBuilder).createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = session.createConsumer(session.createQueue(queueName));
            consumer.setMessageListener(this);
            connection.start();
            LOGGER.info("Started");
        } catch (Exception e) {
            LOGGER.error("Error starting", e);
        }
        startedConnection = connection;
    }

    @Override
    public void onMessage(Message message) {
        String jmsMessageID;
        try {
            jmsMessageID = message.getJMSMessageID();
        } catch (Exception e) {
            jmsMessageID = "unknown (error occurred)";
        }

        try {
            String messageText = ((TextMessage) message).getText();
            if (StringUtils.isNotBlank(messageText)) {
                OBJECT_MAPPER.readValue(messageText, StructuredMessage.class);
                LOGGER.info(String.format("Received message: %s", messageText));
            }

        } catch (Exception e) {
            LOGGER.error(String.format("Unhandled exception while handling message: messageId=%s", jmsMessageID), e);

        } finally {
            try {
                message.acknowledge();
            } catch (Exception e) {
                LOGGER.error(String.format("Unhandled exception while acknowledging message: messageId=%s", jmsMessageID), e);
            }
        }
    }
}
