package com.example.application.ut.ui.view;

import com.example.application.ui.view.MainView;
import com.vaadin.browserless.BrowserlessTest;
import com.vaadin.flow.component.Key;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainViewTest extends BrowserlessTest {

    private MainViewTester view;

    @BeforeEach
    void open() {
        view = new MainViewTester(navigate(MainView.class));
    }

    @Test
    void greetButton_withName_addsCard() {
        view.greet("Alice");
        assertEquals(1, view.getCardCount());
    }

    @Test
    void greetButton_twice_addsTwoCards() {
        view.greet("Alice");
        view.greet("Bob");
        assertEquals(2, view.getCardCount());
    }

    @Test
    void greetButton_withEmptyName_addsAnonymousCard() {
        view.greet("");
        assertEquals(1, view.getCardCount());
        assertEquals("Hello, anonymous user.", view.getCardTesters().getFirst().getMessage());
    }

    @Test
    void greetButton_withName_showsCorrectMessage() {
        view.greet("Alice");
        assertEquals("Hello, Alice.", view.getCardTesters().getFirst().getMessage());
    }

    @Test
    void greetButton_withName_cardShowsTimestamp() {
        view.greet("Alice");
        assertTrue(view.getCardTesters().getFirst().getTimestamp()
                .matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}"));
    }

    @Test
    void enterKey_withName_addsCard() {
        view.setName("Alice");
        fireShortcut(Key.ENTER);
        assertEquals(1, view.getCardCount());
    }

    @Test
    void closeButton_removesCard() {
        view.greet("Alice");
        assertEquals(1, view.getCardCount());
        view.getCardTesters().getFirst().close();
        assertEquals(0, view.getCardCount());
    }
}
