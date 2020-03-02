package com.example.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.util.Utils;

public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        try {
            doSomething();
        } catch (Exception e) {
            LOGGER.error("An error occurred", e);
        }
    }

    private static void doSomething() {
        try {
            Utils.doSomethingThatThrowsAnException();
        } catch (Exception e) {
            throw new RuntimeException("error trying to get something done", e);
        }
    }
}
