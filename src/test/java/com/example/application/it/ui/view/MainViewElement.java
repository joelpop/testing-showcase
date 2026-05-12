package com.example.application.it.ui.view;

import com.example.application.it.ui.component.GreetingCardElement;
import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.Element;

import java.util.List;

@Element("vaadin-vertical-layout")
public class MainViewElement extends TestBenchElement {

    public void setName(String name) {
        $(TextFieldElement.class).single().setValue(name);
    }

    public void clickGreetButton() {
        $(ButtonElement.class).all().stream()
                .filter(b -> "Say hello".equals(b.getText()))
                .findFirst()
                .orElseThrow()
                .click();
    }

    public void greet(String name) {
        setName(name);
        clickGreetButton();
    }

    public void pressEnterInNameField() {
        $(TextFieldElement.class).single().sendKeys(org.openqa.selenium.Keys.ENTER);
    }

    public List<GreetingCardElement> getCards() {
        return $(GreetingCardElement.class).all();
    }

    public int getCardCount() {
        return getCards().size();
    }
}
