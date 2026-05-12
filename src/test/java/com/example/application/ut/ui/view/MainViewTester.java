package com.example.application.ut.ui.view;

import com.example.application.ui.component.GreetingCard;
import com.example.application.ui.view.MainView;
import com.example.application.ut.ui.component.GreetingCardTester;
import com.vaadin.browserless.ComponentTester;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextField;

import java.util.List;

public class MainViewTester extends ComponentTester<MainView> {

    public MainViewTester(MainView component) {
        super(component);
    }

    public void setName(String name) {
        find(TextField.class).single().setValue(name);
    }

    public void clickGreetButton() {
        Button greetButton = find(Button.class).all().stream()
                .filter(b -> "Say hello".equals(b.getText()))
                .findFirst()
                .orElseThrow();
        new ComponentTester<>(greetButton).click();
    }

    public void greet(String name) {
        setName(name);
        clickGreetButton();
    }

    public List<GreetingCardTester> getCardTesters() {
        return find(GreetingCard.class).all().stream()
                .map(GreetingCardTester::new)
                .toList();
    }

    public int getCardCount() {
        return find(GreetingCard.class).all().size();
    }
}
