package com.example.application.model;

import java.time.LocalDateTime;

/**
 * Immutable greeting with a display message and the instant it was created.
 *
 * @param message   the greeting text to display
 * @param timestamp the moment the greeting was created
 */
public record Greeting(String message, LocalDateTime timestamp) {

    /**
     * Creates a greeting addressed to {@code name}, timestamped now.
     *
     * @param name the recipient's name
     */
    public Greeting(String name) {
        this("Hello, " + name + ".", LocalDateTime.now());
    }
}
