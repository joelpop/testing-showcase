package com.example.application.service;

import com.example.application.model.Greeting;

public class GreetService {

    public Greeting greet(String name) {
        var recipientName = ((name == null) || name.isBlank())
                ? "anonymous user"
                : name.trim();

        return new Greeting(recipientName);
    }
}
