package com.example.application.ut.ui.view;

import com.example.application.ui.component.GreetingCard;
import com.example.application.ui.view.MainView;
import com.example.application.ut.ui.component.GreetingCardTester;
import com.vaadin.browserless.ComponentTester;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonTester;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldTester;

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
        return getGreetingCardTesters().size();
    }

    /**
     * Types the given name into the name text field.
     *
     * @param name the name to enter
     */
    public void setName(String name) {
        getNameTextFieldTester().setValue(name);
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


    // INTERNAL component tester accessors

    private List<GreetingCardTester> getGreetingCardTesters() {
        return find(GreetingCard.class).all().stream()
                .map(GreetingCardTester::new)
                .toList();
    }

    private TextFieldTester<TextField, String> getNameTextFieldTester() {
        return new TextFieldTester<>(find(TextField.class).withCaption("Your name").single());
    }

    private ButtonTester<Button> getGreetButtonTester() {
        return new ButtonTester<>(find(Button.class).withText("Say hello").single());
    }


    // INTERNAL helpers

    /**
     * Clicks the "Say hello" button.
     *
     * @return a tester for the newly added greeting card
     */
    private GreetingCardTester clickGreetButton() {
        getGreetButtonTester().click();
        return getGreetingCardTesters().getLast();
    }
}
