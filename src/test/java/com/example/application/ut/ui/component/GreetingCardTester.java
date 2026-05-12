package com.example.application.ut.ui.component;

import com.example.application.ui.component.GreetingCard;
import com.vaadin.browserless.ComponentTester;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

public class GreetingCardTester extends ComponentTester<GreetingCard> {

    public GreetingCardTester(GreetingCard component) {
        super(component);
    }

    public String getMessage() {
        return find(Div.class).single().getText();
    }

    public String getTimestamp() {
        return headerChild(Span.class).getText();
    }

    public void close() {
        new ComponentTester<>(headerChild(Button.class)).click();
    }

    private <T extends Component> T headerChild(Class<T> type) {
        return getComponent().getContent().getHeader()
                .getChildren()
                .filter(type::isInstance)
                .map(type::cast)
                .findFirst()
                .orElseThrow();
    }
}
