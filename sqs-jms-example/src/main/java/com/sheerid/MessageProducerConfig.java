package com.sheerid;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.amazonaws.regions.Regions;

@Component
@ConfigurationProperties(prefix = "message-producer")
public class MessageProducerConfig {
    private Regions region;
    private String topicArn;

    public Regions getRegion() {
        return region;
    }

    public MessageProducerConfig setRegion(Regions region) {
        this.region = region;
        return this;
    }

    public String getTopicArn() {
        return topicArn;
    }

    public MessageProducerConfig setTopicArn(String topicArn) {
        this.topicArn = topicArn;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.serializeSafely(this, "(error while serializing)");
    }

}
