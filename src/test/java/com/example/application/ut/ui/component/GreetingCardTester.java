package com.example.application.ut.ui.component;

import com.example.application.ui.component.GreetingCard;
import com.vaadin.browserless.ComponentTester;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

/**
 * Browserless page object for {@link com.example.application.ui.component.GreetingCard}.
 *
 * <p>Exposes the card's message, header timestamp, and close action.
 * Header children (the {@code Span} and close {@code Button}) live in the {@code Card}'s
 * header slot, which is invisible to {@code ComponentTester.find()} — they are reached
 * directly via {@code Card.getHeader().getChildren()}.
 */
public class GreetingCardTester extends ComponentTester<GreetingCard> {

    // PUBLIC API

    /**
     * Creates a tester for the given greeting card component.
     *
     * @param component the card component to wrap
     */
    public GreetingCardTester(GreetingCard component) {
        super(component);
    }

    /**
     * Returns the greeting message text displayed on the card.
     *
     * @return the message text
     */
    public String getMessage() {
        return getMessageDiv().getText();
    }

    /**
     * Returns the formatted timestamp displayed in the card header.
     *
     * @return the timestamp text
     */
    public String getTimestamp() {
        return getTimestampSpan().getText();
    }

    /**
     * Clicks the close button to remove the card from its parent.
     */
    public void close() {
        new ComponentTester<>(getCloseButton()).click();
    }


    // INTERNAL component accessors

    private Span getTimestampSpan() {
        return headerChild(Span.class);
    }

    private Button getCloseButton() {
        return headerChild(Button.class);
    }

    private Div getMessageDiv() {
        return find(Div.class).single();
    }


    // INTERNAL helpers

    private <T extends Component> T headerChild(Class<T> type) {
        return getComponent().getContent().getHeader()
                .getChildren()
                .filter(type::isInstance)
                .map(type::cast)
                .findFirst()
                .orElseThrow();
    }
}
