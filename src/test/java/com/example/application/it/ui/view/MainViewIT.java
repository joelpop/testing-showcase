package com.example.application.it.ui.view;

import com.example.application.it.ServerExtension;
import com.vaadin.testbench.BrowserTest;
import com.vaadin.testbench.BrowserTestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;

/**
 * TestBench end-to-end tests for {@link com.example.application.ui.view.MainView},
 * using {@link MainViewElement} as the page object.
 *
 * <p>Runs in a real Chrome browser against a Jetty instance on the port configured
 * by the {@code it-deployment.port} POM property. {@link ServerExtension} starts
 * the server automatically when it is not already running (e.g., when launched
 * from an IDE rather than via {@code mvn verify -Pit}).
 * Covers the same cases as {@link com.example.application.ut.ui.view.MainViewTest},
 * plus scroll-into-view behavior that requires real browser rendering.
 */
@ExtendWith(ServerExtension.class)
class MainViewIT extends BrowserTestBase {

    private static String getBaseUrl() {
        var hostname = Optional.ofNullable(System.getenv("HOSTNAME"))
                .filter(h -> !h.isBlank())
                .orElse("localhost");
        String port = System.getProperty("deployment.port");
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
     * Verifies pressing Enter in the name field is equivalent to clicking
     * Say hello.
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
     * Verifies the name field has focus when the view first opens.
     */
    @BrowserTest
    void openView_nameFieldIsFocused() {
        waitUntil(_ -> view.isNameFieldFocused());
        Assertions.assertTrue(view.isNameFieldFocused());
    }

    /**
     * Verifies clicking Say hello returns focus to the name field with the
     * prior name selected.
     */
    @BrowserTest
    void greetButton_focusesAndSelectsNameField() {
        view.greet("Alice");
        waitUntil(_ -> view.getCardCount() == 1);
        Assertions.assertTrue(view.isNameFieldFocused());
        Assertions.assertTrue(view.isNameFieldTextSelected());
    }

    /**
     * Verifies pressing Enter in the name field returns focus to the name
     * field with the prior name selected.
     */
    @BrowserTest
    void enterKey_withName_focusesAndSelectsNameField() {
        view.setName("Alice");
        view.pressEnterInNameField();
        waitUntil(_ -> view.getCardCount() == 1);
        Assertions.assertTrue(view.isNameFieldFocused());
        Assertions.assertTrue(view.isNameFieldTextSelected());
    }

    /**
     * Verifies pressing Enter in the name field with no name entered returns
     * focus to the name field without selecting any text.
     */
    @BrowserTest
    void enterKey_withEmptyName_focusesNameFieldWithoutSelection() {
        view.pressEnterInNameField();
        waitUntil(_ -> view.getCardCount() == 1);
        Assertions.assertTrue(view.isNameFieldFocused());
        Assertions.assertFalse(view.isNameFieldTextSelected());
    }

    /**
     * Verifies the newest card is automatically scrolled into the visible area
     * after the list grows beyond the scroller's bounds.
     */
    @BrowserTest
    void greetButton_scrollsNewestCardIntoView() {
        var firstCard = view.greet("User 1");
        int num = 2;
        while (num <= 100 && view.isCardVisible(firstCard)) {
            view.greet("User " + num++);
        }
        var cardCount = num - 1;

        Assertions.assertEquals(cardCount, view.getCardCount());
        var newestGreetingCardElement = view.getGreetingCardElements().getLast();
        waitUntil(_ -> view.isCardVisible(newestGreetingCardElement));
        Assertions.assertTrue(view.isCardVisible(newestGreetingCardElement));
    }
}
