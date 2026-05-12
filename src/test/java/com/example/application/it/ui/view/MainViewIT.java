package com.example.application.it.ui.view;

import com.vaadin.testbench.BrowserTest;
import com.vaadin.testbench.BrowserTestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;

/**
 * TestBench end-to-end tests for {@link com.example.application.ui.view.MainView},
 * using {@link MainViewElement} as the page object.
 *
 * <p>Runs in a real Chrome browser against a Jetty instance started on port 9090.
 * Covers the same cases as {@link com.example.application.ut.ui.view.MainViewTest},
 * plus scroll-into-view behavior that requires real browser rendering.
 */
class MainViewIT extends BrowserTestBase {

    private static String getBaseUrl() {
        var hostname = Optional.ofNullable(System.getenv("HOSTNAME"))
                .filter(h -> !h.isBlank())
                .orElse("localhost");
        String port = System.getProperty("deployment.port", "9090");
        return "http://" + hostname + ":" + port + "/";
    }

    private MainViewElement view;

    @BeforeEach
    void open() {
        getDriver().get(getBaseUrl());
        view = $(MainViewElement.class).onPage().get(0);
    }

    /**
     * Verifies clicking Say hello with a name adds one card to the list.
     */
    @BrowserTest
    void greetButton_withName_addsCard() {
        view.greet("Alice");
        Assertions.assertEquals(1, view.getCardCount());
    }

    /**
     * Verifies each click adds a separate card.
     */
    @BrowserTest
    void greetButton_twice_addsTwoCards() {
        view.greet("Alice");
        view.greet("Bob");
        Assertions.assertEquals(2, view.getCardCount());
    }

    /**
     * Verifies an empty name produces a card addressed to "anonymous user".
     */
    @BrowserTest
    void greetButton_withEmptyName_addsAnonymousCard() {
        var card = view.greet("");
        Assertions.assertEquals(1, view.getCardCount());
        Assertions.assertEquals("Hello, anonymous user.", card.getMessage());
    }

    /**
     * Verifies the card displays the correctly formatted greeting message.
     */
    @BrowserTest
    void greetButton_withName_showsCorrectMessage() {
        Assertions.assertEquals("Hello, Alice.", view.greet("Alice").getMessage());
    }

    /**
     * Verifies the card header displays a timestamp matching the expected format.
     */
    @BrowserTest
    void greetButton_withName_cardShowsTimestamp() {
        Assertions.assertTrue(view.greet("Alice").getTimestamp()
                .matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}"));
    }

    /**
     * Verifies pressing Enter in the name field is equivalent to clicking Say hello.
     */
    @BrowserTest
    void enterKey_withName_addsCard() {
        view.setName("Alice");
        view.pressEnterInNameField();
        Assertions.assertEquals(1, view.getCardCount());
    }

    /**
     * Verifies clicking the card's close button removes it from the list.
     */
    @BrowserTest
    void closeButton_removesCard() {
        var card = view.greet("Alice");
        Assertions.assertEquals(1, view.getCardCount());
        card.close();
        Assertions.assertEquals(0, view.getCardCount());
    }

    /**
     * Verifies the newest card is automatically scrolled into the visible area
     * after the list grows beyond the scroller's bounds.
     */
    @BrowserTest
    void greetButton_scrollsNewestCardIntoView() {
        var firstCard = view.greet("User 1");
        for (int i = 2; i <= 100 && view.isCardVisible(firstCard); i++) {
            view.greet("User " + i);
        }
        var newestCard = view.getCards().getLast();
        waitUntil(_ -> view.isCardVisible(newestCard));
    }
}
