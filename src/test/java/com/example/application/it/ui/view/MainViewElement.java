package com.example.application.it.ui.view;

import com.example.application.it.ui.component.GreetingCardElement;
import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.orderedlayout.testbench.ScrollerElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.Element;

import java.util.List;

/**
 * TestBench page object for {@link com.example.application.ui.view.MainView}.
 *
 * <p>Exposes the name field, greet button, Enter-key shortcut, and card list so e2e tests
 * can interact with the view through stable, intention-revealing methods rather than raw
 * DOM queries. Mirrors the API of {@link com.example.application.ut.ui.view.MainViewTester}
 * so that the browserless and e2e test suites remain structurally identical.
 */
@Element("vaadin-vertical-layout")
public class MainViewElement extends TestBenchElement {

    // PUBLIC API

    /**
     * Types the given name into the name text field.
     *
     * @param name the name to enter
     */
    public void setName(String name) {
        getNameTextFieldElement().click();
        getNameTextFieldElement().sendKeys(name);
    }

    /**
     * Types the given name and clicks "Say hello".
     *
     * @param name the name to greet
     * @return an element for the newly added greeting card
     */
    public GreetingCardElement greet(String name) {
        setName(name);
        return clickGreetButton();
    }

    /**
     * Presses Enter in the name text field.
     */
    public void pressEnterInNameField() {
        getNameTextFieldElement().sendKeys(org.openqa.selenium.Keys.ENTER);
    }

    /**
     * Returns all greeting card elements currently displayed in the list.
     *
     * @return list of card elements, in display order
     */
    public List<GreetingCardElement> getGreetingCardElements() {
        return $(GreetingCardElement.class).all();
    }

    /**
     * Returns the number of greeting cards currently displayed in the list.
     *
     * @return card count
     */
    public int getCardCount() {
        return getGreetingCardElements().size();
    }

    /**
     * Returns whether the name text field currently has keyboard focus.
     *
     * @return {@code true} if the name field is focused
     */
    public boolean isNameFieldFocused() {
        return getNameTextFieldElement().hasAttribute("focused");
    }

    /**
     * Returns whether the name text field currently has a non-empty text selection.
     *
     * @return {@code true} if one or more characters are selected in the name field
     */
    public boolean isNameFieldTextSelected() {
        return (Boolean) executeScript(
                "const el = arguments[0].inputElement;" +
                "return el.value.length > 0 && el.selectionStart === 0 && el.selectionEnd === el.value.length;",
                getNameTextFieldElement());
    }

    /**
     * Returns whether the given card is fully within the visible bounds of the scroller.
     *
     * @param card the card to check
     * @return {@code true} if the card is within the scroller's visible area
     */
    public boolean isCardVisible(GreetingCardElement card) {
        var scrollerRect = getScrollerElement().getRect();
        var cardRect = card.getRect();
        return cardRect.getY() + cardRect.getHeight() <= scrollerRect.getY() + scrollerRect.getHeight() + 1
            && cardRect.getY() >= scrollerRect.getY() - 1;
    }


    // INTERNAL element accessors

    private ScrollerElement getScrollerElement() {
        return $(ScrollerElement.class).single();
    }

    private TextFieldElement getNameTextFieldElement() {
        return $(TextFieldElement.class).withCaption("Your name").single();
    }

    private ButtonElement getGreetButtonElement() {
        return $(ButtonElement.class).withText("Say hello").single();
    }


    // INTERNAL helpers

    /**
     * Clicks the "Say hello" button.
     *
     * @return an element for the newly added greeting card
     */
    private GreetingCardElement clickGreetButton() {
        getGreetButtonElement().click();
        return getGreetingCardElements().getLast();
    }
}
