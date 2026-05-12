package com.example.application.it.ui.component;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.card.testbench.CardElement;
import com.vaadin.flow.component.html.testbench.SpanElement;
import com.vaadin.flow.component.orderedlayout.testbench.ScrollerElement;
import com.vaadin.testbench.elementsbase.Element;

@Element("vaadin-card")
public class GreetingCardElement extends CardElement {

    public String getMessage() {
        return getContents().getLast().getText();
    }

    public String getTimestamp() {
        return getHeader().$(SpanElement.class).single().getText();
    }

    public void close() {
        getHeader().$(ButtonElement.class).single().click();
    }

    public boolean isVisibleInScroller() {
        var scrollerRect = $(ScrollerElement.class).onPage().single().getRect();
        var cardRect = getRect();
        return cardRect.getY() + cardRect.getHeight() <= scrollerRect.getY() + scrollerRect.getHeight() + 1
            && cardRect.getY() >= scrollerRect.getY() - 1;
    }
}
