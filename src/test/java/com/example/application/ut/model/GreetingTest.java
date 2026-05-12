package com.example.application.ut.model;

import com.example.application.model.Greeting;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link com.example.application.model.Greeting}.
 *
 * <p>Covers the name-based convenience constructor (message format and timestamp capture)
 * and the canonical constructor (field preservation).
 */
class GreetingTest {

    /**
     * Verifies the greeting message is formatted as "Hello, {name}."
     */
    @Test
    void nameConstructor_setsCorrectMessage() {
        var greeting = new Greeting("Alice");
        assertEquals("Hello, Alice.", greeting.message());
    }

    /**
     * Verifies the timestamp is set to the time of construction,
     * bounded by measurements taken immediately before and after.
     */
    @Test
    void nameConstructor_setsTimestamp() {
        var before = LocalDateTime.now();
        var greeting = new Greeting("Alice");
        var after = LocalDateTime.now();
        assertNotNull(greeting.timestamp());
        assertFalse(greeting.timestamp().isBefore(before));
        assertFalse(greeting.timestamp().isAfter(after));
    }

    /**
     * Verifies the canonical constructor stores the provided message and timestamp unchanged.
     */
    @Test
    void canonicalConstructor_preservesFields() {
        var timestamp = LocalDateTime.of(2025, 6, 15, 12, 0, 0);
        var greeting = new Greeting("Custom message.", timestamp);
        assertEquals("Custom message.", greeting.message());
        assertEquals(timestamp, greeting.timestamp());
    }
}
