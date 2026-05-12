package com.example.application.ut.ui.view;

import com.example.application.ui.component.GreetingCard;
import com.example.application.ui.view.MainView;
import com.example.application.ut.ui.component.GreetingCardTester;
import com.vaadin.browserless.ComponentTester;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextField;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Browserless page object for {@link com.example.application.ui.view.MainView}.
 *
 * <p>Exposes the name field, greet button, and card list so tests can interact with the
 * view through stable, intention-revealing methods rather than raw component lookups.
 * Mirrors the API of {@link com.example.application.it.ui.view.MainViewElement} so that
 * the browserless and e2e test suites remain structurally identical.
 */
public class MainViewTester extends ComponentTester<MainView> {

    // PUBLIC API

    /**
     * Creates a tester for the given main view component.
     *
     * @param component the view component to wrap
     */
    public MainViewTester(MainView component) {
        super(component);
    }

    /**
     * Returns the number of greeting cards currently displayed in the list.
     *
     * @return card count
     */
    public int getCardCount() {
        return getGreetingCards().size();
    }

    /**
     * Types the given name into the name text field.
     *
     * @param name the name to enter
     */
    public void setName(String name) {
        getNameTextField().setValue(name);
    }

    /**
     * Types the given name and clicks "Say hello".
     *
     * @param name the name to greet
     * @return a tester for the newly added greeting card
     */
    public GreetingCardTester greet(String name) {
        setName(name);
        return clickGreetButton();
    }


    // INTERNAL component accessors

    private List<GreetingCard> getGreetingCards() {
        return find(GreetingCard.class).all();
    }

    private TextField getNameTextField() {
        return find(TextField.class).withCaption("Your name").single();
    }

    private @NonNull Button getGreetButton() {
        return find(Button.class).withText("Say hello").single();
    }

    // INTERNAL helpers

    /*
     * Clicks the "Say hello" button.
     *
     * @return a tester for the newly added greeting card
     */
    private GreetingCardTester clickGreetButton() {
        new ComponentTester<>(getGreetButton()).click();
        return getCardTesters().getLast();
    }

    /*
     * Returns testers for all greeting cards currently displayed in the list.
     *
     * @return list of card testers, in display order
     */
    private List<GreetingCardTester> getCardTesters() {
        return getGreetingCards().stream()
                .map(GreetingCardTester::new)
                .toList();
    }
}
