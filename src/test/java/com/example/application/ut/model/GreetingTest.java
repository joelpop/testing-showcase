package com.example.application.ut.model;

import com.example.application.model.Greeting;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class GreetingTest {

    @Test
    void nameConstructor_setsCorrectMessage() {
        var greeting = new Greeting("Alice");
        assertEquals("Hello, Alice.", greeting.message());
    }

    @Test
    void nameConstructor_setsTimestamp() {
        var before = LocalDateTime.now();
        var greeting = new Greeting("Alice");
        var after = LocalDateTime.now();
        assertNotNull(greeting.timestamp());
        assertFalse(greeting.timestamp().isBefore(before));
        assertFalse(greeting.timestamp().isAfter(after));
    }

    @Test
    void canonicalConstructor_preservesFields() {
        var timestamp = LocalDateTime.of(2025, 6, 15, 12, 0, 0);
        var greeting = new Greeting("Custom message.", timestamp);
        assertEquals("Custom message.", greeting.message());
        assertEquals(timestamp, greeting.timestamp());
    }
}
