package com.example.application.ut.service;

import com.example.application.service.GreetService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GreetServiceTest {

    private final GreetService greetService = new GreetService();

    @Test
    void greet_withName_returnsGreetingForName() {
        var greeting = greetService.greet("Alice");
        assertEquals("Hello, Alice.", greeting.message());
    }

    @Test
    void greet_withNull_returnsGreetingForAnonymousUser() {
        var greeting = greetService.greet(null);
        assertEquals("Hello, anonymous user.", greeting.message());
    }

    @Test
    void greet_withBlank_returnsGreetingForAnonymousUser() {
        var greeting = greetService.greet("   ");
        assertEquals("Hello, anonymous user.", greeting.message());
    }

    @Test
    void greet_withPaddedName_returnsTrimmedName() {
        var greeting = greetService.greet("  Alice  ");
        assertEquals("Hello, Alice.", greeting.message());
    }
}
