package com.sheerid;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StructuredMessage {
    private final String id;
    private final Date date;

    @JsonCreator
    public StructuredMessage(@JsonProperty("id") String id, @JsonProperty("date") Date date) {
        this.id = id;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }
}
