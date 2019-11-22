package com.sheerid;

import java.util.logging.LogManager;

import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
@SpringBootApplication
public class Application implements ApplicationRunner {

    public static void main(String[] args) {
        LogManager.getLogManager()
                  .reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        new SpringApplicationBuilder(Application.class).bannerMode(Banner.Mode.OFF)
                                                       .run(args);
    }

    private final MessageProducer messageProducer;
    private final JmsSqsMessageHandler jmsSqsMessageHandler;
    private final PlainSqsMessageHandler plainSqsMessageHandler;

    @Autowired
    public Application(MessageProducer messageProducer,
                       JmsSqsMessageHandler jmsSqsMessageHandler,
                       PlainSqsMessageHandler plainSqsMessageHandler) {
        this.messageProducer = messageProducer;
        this.jmsSqsMessageHandler = jmsSqsMessageHandler;
        this.plainSqsMessageHandler = plainSqsMessageHandler;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Uncomment one of the following message handlers
        //jmsMessageHandler.start();
        plainSqsMessageHandler.start();

        messageProducer.start();
    }
}
