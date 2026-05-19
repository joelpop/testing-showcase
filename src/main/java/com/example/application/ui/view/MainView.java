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
import com.vaadin.flow.component.orderedlayout.ScrollerVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * Root view: scrollable list of greeting cards above a name-input area.
 *
 * <pre>
 * +-content(VerticalLayout)---------------+
 * | +-scroller--------------------------+ |
 * | | +-cardsLayout(VerticalLayout)---+ | |
 * | | | +-card(GreetingCard)--------+ | | |
 * | | | | (see GreetingCard)        | | | |
 * | | | +---------------------------+ | | |
 * | | | +-card(GreetingCard)--------+ | | |
 * | | | | (see GreetingCard)        | | | |
 * | | | +---------------------------+ | | |
 * | | |               .               | | |
 * | | |               .               | | |
 * | | |               .               | | |
 * | | +-------------------------------+ | |
 * | +-----------------------------------+ |
 * | +-inputArea(HorizontalLayout)-------+ |
 * | |  Your name                        | |
 * | | +-nameTextField-+ +-greetButton-+ | |
 * | | |               | |  Say hello  | | |
 * | | +---------------+ +-------------+ | |
 * | +-----------------------------------+ |
 * +---------------------------------------+
 * </pre>
 */
@Route("")
public class MainView extends Composite<VerticalLayout> {

    private final VerticalLayout cardsLayout;
    private final transient GreetService greetService;
    private final TextField nameTextField;

    public MainView() {
        greetService = new GreetService();

        nameTextField = new TextField("Your name");
        nameTextField.setAutoselect(true);
        addAttachListener(_ -> nameTextField.focus());

        var greetButton = new Button("Say hello");
        greetButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        greetButton.addClickListener(this::onGreetButtonClick);
        greetButton.addClickShortcut(Key.ENTER);

        cardsLayout = new VerticalLayout();
        cardsLayout.setWidthFull();

        var scroller = new Scroller(cardsLayout);
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        scroller.setWidthFull();
        scroller.addThemeVariants(ScrollerVariant.OVERFLOW_INDICATORS);
        scroller.getStyle().set("scroll-padding", "var(--lumo-space-m)");

        var inputArea = new HorizontalLayout(nameTextField, greetButton);
        inputArea.setWidthFull();
        inputArea.setPadding(true);
        inputArea.addClassName(LumoUtility.Padding.Top.NONE);
        inputArea.setAlignItems(FlexComponent.Alignment.END);
        inputArea.setFlexGrow(1, nameTextField);

        var content = getContent();
        content.setSizeFull();
        content.setPadding(false);
        content.setSpacing(false);
        content.setMaxWidth("25em");
        content.addClassName(LumoUtility.Margin.Horizontal.AUTO);
        content.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.BorderRadius.LARGE);
        content.add(scroller, inputArea);
        content.setFlexGrow(1, scroller);
    }

    /*
     * Creates a greeting card for the current name field value, adds it to the list,
     * smoothly scrolls it into view, and refocuses on the name field.
     */
    private void onGreetButtonClick(ClickEvent<Button> ignored) {
        var card = new GreetingCard(greetService.greet(nameTextField.getValue()));
        cardsLayout.add(card);
        card.scrollIntoView(ScrollIntoViewOption.Behavior.SMOOTH, ScrollIntoViewOption.Block.NEAREST);
        nameTextField.blur();  // blur so subsequent focus will select name text when using key shortcut
        nameTextField.focus();
    }
}
