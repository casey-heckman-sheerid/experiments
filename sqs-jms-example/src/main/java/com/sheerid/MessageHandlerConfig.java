package com.sheerid;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.amazonaws.regions.Regions;

@Component
@ConfigurationProperties(prefix = "message-handler")
public class MessageHandlerConfig {
    private Regions region;
    private String queueUrl;

    public Regions getRegion() {
        return region;
    }

    public MessageHandlerConfig setRegion(Regions region) {
        this.region = region;
        return this;
    }

    public String getQueueUrl() {
        return queueUrl;
    }

    public MessageHandlerConfig setQueueUrl(String queueUrl) {
        this.queueUrl = queueUrl;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.serializeSafely(this, "(error while serializing)");
    }
}
