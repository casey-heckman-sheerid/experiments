package com.sheerid.a;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sheerid.b.B;

public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        try {
            B.doit();
        } catch (Exception e){
            LOGGER.error("our message", e);
        }
    }
}
