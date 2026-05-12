package com.example.application.ut.service;

import com.example.application.service.GreetService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link com.example.application.service.GreetService}.
 *
 * <p>Covers name resolution: valid names, null, blank, and whitespace-padded input.
 */
class GreetServiceTest {

    private final GreetService greetService = new GreetService();

    /**
     * Verifies a non-blank name produces a correctly addressed greeting message.
     */
    @Test
    void greet_withName_returnsGreetingForName() {
        var greeting = greetService.greet("Alice");
        assertEquals("Hello, Alice.", greeting.message());
    }

    /**
     * Verifies null input is substituted with "anonymous user".
     */
    @Test
    void greet_withNull_returnsGreetingForAnonymousUser() {
        var greeting = greetService.greet(null);
        assertEquals("Hello, anonymous user.", greeting.message());
    }

    /**
     * Verifies blank (whitespace-only) input is substituted with "anonymous user".
     */
    @Test
    void greet_withBlank_returnsGreetingForAnonymousUser() {
        var greeting = greetService.greet("   ");
        assertEquals("Hello, anonymous user.", greeting.message());
    }

    /**
     * Verifies surrounding whitespace is stripped from the name before greeting.
     */
    @Test
    void greet_withPaddedName_returnsTrimmedName() {
        var greeting = greetService.greet("  Alice  ");
        assertEquals("Hello, Alice.", greeting.message());
    }
}
