package com.example.application.model;

import java.time.LocalDateTime;

public record Greeting(String message, LocalDateTime timestamp) {

    public Greeting(String name) {
        this("Hello, " + name + ".", LocalDateTime.now());
    }
}
