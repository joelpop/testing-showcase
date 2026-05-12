package com.example.application.it.ui.component;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.card.testbench.CardElement;
import com.vaadin.flow.component.html.testbench.DivElement;
import com.vaadin.flow.component.html.testbench.SpanElement;
import com.vaadin.testbench.elementsbase.Element;

/**
 * TestBench page object for {@link com.example.application.ui.component.GreetingCard}.
 *
 * <p>Extends {@code CardElement} to use its slot-aware DOM accessors: {@code getContents()}
 * for the message in the default slot and {@code getHeader()} for the timestamp and close
 * button in the header slot.
 */
@Element("vaadin-card")
public class GreetingCardElement extends CardElement {

    // PUBLIC API

    /**
     * Returns the greeting message text displayed on the card.
     *
     * @return the message text
     */
    public String getMessage() {
        return getMessageDivElement().getText();
    }

    /**
     * Returns the formatted timestamp displayed in the card header.
     *
     * @return the timestamp text
     */
    public String getTimestamp() {
        return getTimestampSpanElement().getText();
    }

    /**
     * Clicks the close button to remove the card.
     */
    public void close() {
        getCloseButtonElement().click();
    }


    // INTERNAL element accessors

    private SpanElement getTimestampSpanElement() {
        return getHeader().$(SpanElement.class).single();
    }

    private ButtonElement getCloseButtonElement() {
        return getHeader().$(ButtonElement.class).single();
    }

    private DivElement getMessageDivElement() {
        return getContents().getLast().wrap(DivElement.class);
    }
}
