package com.example.application.ut.ui.view;

import com.example.application.ui.view.MainView;
import com.vaadin.browserless.BrowserlessTest;
import com.vaadin.flow.component.Key;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Browserless unit tests for {@link com.example.application.ui.view.MainView},
 * using {@link MainViewTester} as the page object.
 *
 * <p>Runs entirely in the JVM via {@code BrowserlessTest} — no browser or servlet container
 * required. Covers button clicks, Enter key shortcut, empty-name handling, card message
 * and timestamp content, and card removal.
 */
class MainViewTest extends BrowserlessTest {

    private MainViewTester view;

    @BeforeEach
    void open() {
        view = new MainViewTester(navigate(MainView.class));
    }

    /**
     * Verifies clicking Say hello with a name adds one card to the list.
     */
    @Test
    void greetButton_withName_addsCard() {
        view.greet("Alice");
        assertEquals(1, view.getCardCount());
    }

    /**
     * Verifies each click adds a separate card.
     */
    @Test
    void greetButton_twice_addsTwoCards() {
        view.greet("Alice");
        view.greet("Bob");
        assertEquals(2, view.getCardCount());
    }

    /**
     * Verifies an empty name produces a card addressed to "anonymous user".
     */
    @Test
    void greetButton_withEmptyName_addsAnonymousCard() {
        var card = view.greet("");
        assertEquals(1, view.getCardCount());
        assertEquals("Hello, anonymous user.", card.getMessage());
    }

    /**
     * Verifies the card displays the correctly formatted greeting message.
     */
    @Test
    void greetButton_withName_showsCorrectMessage() {
        assertEquals("Hello, Alice.", view.greet("Alice").getMessage());
    }

    /**
     * Verifies the card header displays a timestamp matching the expected format.
     */
    @Test
    void greetButton_withName_cardShowsTimestamp() {
        assertTrue(view.greet("Alice").getTimestamp()
                .matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}"));
    }

    /**
     * Verifies pressing Enter in the name field is equivalent to clicking Say hello.
     */
    @Test
    void enterKey_withName_addsCard() {
        view.setName("Alice");
        fireShortcut(Key.ENTER);
        assertEquals(1, view.getCardCount());
    }

    /**
     * Verifies clicking the card's close button removes it from the list.
     */
    @Test
    void closeButton_removesCard() {
        var card = view.greet("Alice");
        assertEquals(1, view.getCardCount());
        card.close();
        assertEquals(0, view.getCardCount());
    }
}
