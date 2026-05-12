package com.example.application.it.ui.view;

import com.vaadin.testbench.BrowserTest;
import com.vaadin.testbench.BrowserTestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;

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

    @BrowserTest
    void greetButton_withName_addsCard() {
        view.greet("Alice");
        Assertions.assertEquals(1, view.getCardCount());
    }

    @BrowserTest
    void greetButton_twice_addsTwoCards() {
        view.greet("Alice");
        view.greet("Bob");
        Assertions.assertEquals(2, view.getCardCount());
    }

    @BrowserTest
    void greetButton_withEmptyName_addsAnonymousCard() {
        view.greet("");
        Assertions.assertEquals(1, view.getCardCount());
        Assertions.assertEquals("Hello, anonymous user.", view.getCards().getFirst().getMessage());
    }

    @BrowserTest
    void greetButton_withName_showsCorrectMessage() {
        view.greet("Alice");
        Assertions.assertEquals("Hello, Alice.", view.getCards().getFirst().getMessage());
    }

    @BrowserTest
    void greetButton_withName_cardShowsTimestamp() {
        view.greet("Alice");
        Assertions.assertTrue(view.getCards().getFirst().getTimestamp()
                .matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}"));
    }

    @BrowserTest
    void enterKey_withName_addsCard() {
        view.setName("Alice");
        view.pressEnterInNameField();
        Assertions.assertEquals(1, view.getCardCount());
    }

    @BrowserTest
    void closeButton_removesCard() {
        view.greet("Alice");
        Assertions.assertEquals(1, view.getCardCount());
        view.getCards().getFirst().close();
        Assertions.assertEquals(0, view.getCardCount());
    }

    @BrowserTest
    void greetButton_scrollsNewestCardIntoView() {
        view.greet("User 1");
        var firstCard = view.getCards().getFirst();
        for (int i = 2; i <= 100 && firstCard.isVisibleInScroller(); i++) {
            view.greet("User " + i);
        }
        var newestCard = view.getCards().getLast();
        waitUntil(_ -> newestCard.isVisibleInScroller());
    }
}
