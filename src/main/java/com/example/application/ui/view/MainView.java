package com.example.application.ui.view;

import com.example.application.service.GreetService;
import com.example.application.ui.component.GreetingCard;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.ScrollIntoViewOption;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route("")
public class MainView extends Composite<VerticalLayout> {

    private final VerticalLayout cardsLayout;
    private final transient GreetService greetService;
    private final TextField nameTextField;

    public MainView() {
        greetService = new GreetService();

        nameTextField = new TextField("Your name");

        var greetButton = new Button("Say hello");
        greetButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        greetButton.addClickListener(this::onGreetButtonClick);
        greetButton.addClickShortcut(Key.ENTER);

        cardsLayout = new VerticalLayout();
        cardsLayout.setWidthFull();

        var scroller = new Scroller(cardsLayout);
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        scroller.setWidthFull();

        var inputArea = new HorizontalLayout(nameTextField, greetButton);
        inputArea.setWidthFull();
        inputArea.setPadding(true);
        inputArea.setAlignItems(FlexComponent.Alignment.END);
        inputArea.setFlexGrow(1, nameTextField);

        var content = getContent();
        content.setSizeFull();
        content.setPadding(false);
        content.setSpacing(false);
        content.setMaxWidth("25em");
        content.addClassName(LumoUtility.Margin.Horizontal.AUTO);
        content.add(scroller, inputArea);
        content.setFlexGrow(1, scroller);
    }

    private void onGreetButtonClick(ClickEvent<Button> ignored) {
        var card = new GreetingCard(greetService.greet(nameTextField.getValue()));
        cardsLayout.add(card);
        card.scrollIntoView(ScrollIntoViewOption.Behavior.SMOOTH, ScrollIntoViewOption.Block.NEAREST);
    }
}
