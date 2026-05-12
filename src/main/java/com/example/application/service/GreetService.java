package com.example.application.service;

import com.example.application.model.Greeting;

/**
 * Produces {@link Greeting} instances from user-supplied names.
 */
public class GreetService {

    /**
     * Returns a greeting for {@code name}, substituting {@code "anonymous user"} if the name is null or blank.
     *
     * @param name the recipient's name; may be null, blank, or padded with whitespace
     * @return a {@link Greeting} addressed to the resolved name
     */
    public Greeting greet(String name) {
        var recipientName = ((name == null) || name.isBlank())
                ? "anonymous user"
                : name.trim();

        return new Greeting(recipientName);
    }
}
