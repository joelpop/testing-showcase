# Testing Showcase

A Vaadin application demonstrating two complementary testing approaches: **browserless unit tests** and **TestBench end-to-end tests**, using page objects that mirror each other.

## The Application

A greeting app where users type a name and click **Say hello** (or press Enter). Each greeting appears as a card with a timestamp and a close button. Cards accumulate in a scrollable list, automatically scrolling the newest card into view.

The name field is focused when the view opens. After each greeting, the name field regains focus with its text selected, so the user can immediately type a new name.

## Running the Application

```
mvn jetty:run
```

Opens at [http://localhost:8080](http://localhost:8080).

## Project Structure

```
src/main/java/com/example/application/
├── model/
│   └── Greeting.java            # Record: message + timestamp
├── service/
│   └── GreetService.java        # Resolves name, returns Greeting
├── ui/
│   ├── component/
│   │   └── GreetingCard.java    # Composite<Card>: displays a Greeting
│   └── view/
│       └── MainView.java        # Root view: scrollable cards + input area
└── AppShell.java
```

---

## Testing

### Unit Tests — `mvn test`

No browser or servlet container required. Two kinds of unit tests:

#### Plain JUnit — model and service

```
src/test/java/com/example/application/
└── ut/
    ├── model/
    │   └── GreetingTest.java        # Tests the Greeting record
    └── service/
        └── GreetServiceTest.java    # Tests GreetService name resolution
```

These are standard JUnit 5 tests with no Vaadin machinery involved.

#### Browserless — UI logic

```
src/test/java/com/example/application/
└── ut/
    └── ui/
        ├── component/
        │   └── GreetingCardTester.java    # Page object (ComponentTester)
        └── view/
            ├── MainViewTester.java        # Page object (ComponentTester)
            └── MainViewTest.java          # Browserless tests
```

`MainViewTest` extends `BrowserlessTest` (from `browserless-test-junit6`). Vaadin's test environment instantiates the UI, session, and routes in the JVM — no browser, no HTTP. Tests call `navigate(MainView.class)` to get a view instance, then interact through the page objects.

The suite covers button clicks, the Enter key shortcut, empty-name handling, card message and timestamp content, and card removal — 7 test cases in total.

---

### Integration Tests — `mvn verify -Pit`

Requires Chrome. Starts Jetty on port **9090** (to avoid colliding with a running dev instance on 8080), runs tests, then stops Jetty.

```
src/test/java/com/example/application/
└── it/
    └── ui/
        ├── component/
        │   └── GreetingCardElement.java   # Page object (TestBenchElement)
        └── view/
            ├── MainViewElement.java       # Page object (TestBenchElement)
            └── MainViewIT.java            # TestBench e2e tests
```

`MainViewIT` extends `BrowserTestBase`. Tests open the app in a real browser and interact through the page objects.

TestBench page objects extend `TestBenchElement` and are annotated with `@Element("tag-name")`. They expose the same high-level API as the browserless page objects — same method names, different implementation. Internally they use `$(ElementType.class)` to query the live DOM.

The suite covers the same 7 cases as the browserless suite, plus 4 that require a real browser:

#### Browser-Only Tests

**Focus and selection** — three tests verify that the name field is focused on view open, and that it is focused with its text selected after clicking the button or pressing Enter. Browserless tests have no concept of browser focus or text selection.

**Scroll visibility** — `greetButton_scrollsNewestCardIntoView` adds cards until the first card scrolls out of view (adapting to actual window size rather than a hardcoded count), then uses `waitUntil()` to confirm the newest card is in the visible area. Scroll position is checked via `TestBenchElement.getRect()`.

---

## Page Object Design

Both page object families follow the same structural conventions.

### Three-section layout

Each page object class is divided into three sections:

```java
// PUBLIC API                          — methods called directly by tests
// INTERNAL component tester accessors — private methods that locate and wrap components (browserless)
// INTERNAL element accessors          — private methods that locate DOM elements (TestBench)
// INTERNAL helpers                    — private methods that combine accessors into operations
```

This keeps the public surface clean and makes component lookups easy to find and update in one place.

### Accessor return types

Internal accessor methods are named and typed to match what they actually return — a `Tester` or `Element` suffix signals that the method returns a testing wrapper, not the raw component:

```java
// Browserless — internal accessors return tester types
private TextFieldTester<TextField, String> getNameTextFieldTester() { ... }
private ButtonTester<Button>              getGreetButtonTester()    { ... }
private SpanTester                        getTimestampSpanTester()  { ... }
private DivTester                         getMessageDivTester()     { ... }

// TestBench — internal accessors return element types
private TextFieldElement getNameTextFieldElement() { ... }
private ButtonElement    getGreetButtonElement()   { ... }
private SpanElement      getTimestampSpanElement() { ... }
private DivElement       getMessageDivElement()    { ... }
```

This prevents confusion when reading the code — a method named `getNameTextField()` that returns a `TextFieldTester` would be misleading.

### User input via Tester and Element APIs

All user interactions in the tests go through the appropriate testing abstraction — never through direct component state manipulation:

```java
// Browserless — TextFieldTester.setValue() checks usability before setting
getNameTextFieldTester().setValue(name);   // not: textField.setValue(name)
getGreetButtonTester().click();            // not: componentTester.click()

// TestBench — click() + sendKeys() simulates real typing (leaves no pre-existing selection)
getNameTextFieldElement().click();
getNameTextFieldElement().sendKeys(name);  // not: textFieldElement.setValue(name)
```

This matters for correctness: `TextFieldElement.setValue()` in TestBench leaves text selected as a side effect, which would mask the absence of the autoselect behavior being tested.

### Stable element location

Components are always located by a user-visible identifier rather than by type alone:

```java
// Browserless
find(TextField.class).withCaption("Your name").single()
find(Button.class).withText("Say hello").single()

// TestBench
$(TextFieldElement.class).withCaption("Your name").single()
$(ButtonElement.class).withText("Say hello").single()
```

This prevents tests from breaking when additional components of the same type are added to the view.

### Method chaining

`greet()` and `clickGreetButton()` return the newly added card, enabling fluent test expressions:

```java
assertEquals("Hello, Alice.", view.greet("Alice").getMessage());
view.greet("Alice").close();
```

### Separation of concerns

Visibility relative to the scroller is a concern of the view, not the card. `isCardVisible(GreetingCardElement)` lives on `MainViewElement` so that `GreetingCardElement` only knows about its own content.

---

## Similarities Between Both Approaches

| | Browserless (`MainViewTest`) | TestBench (`MainViewIT`) |
|---|---|---|
| Page object base | `ComponentTester<T>` | `TestBenchElement` |
| Internal accessor types | `TextFieldTester`, `ButtonTester`, `SpanTester`, `DivTester` | `TextFieldElement`, `ButtonElement`, `SpanElement`, `DivElement` |
| Public method names | `greet()`, `setName()`, `getMessage()`, `getTimestamp()`, `close()`, … | same |
| Test cases | 7 shared cases | 7 shared + 4 browser-only |
| Element location | `find().withCaption()/withText()` | `$().withCaption()/withText()` |
| Run with | `mvn test` | `mvn verify -Pit` |
| Needs browser | no | yes (Chrome) |
| Needs server | no | yes (Jetty on 9090) |
| Speed | ~milliseconds | ~seconds |

## Key Differences

**Browserless tests** run entirely server-side. They are fast, reliable (no Selenium timing issues), and run in CI without a display. They verify server-side logic — component state, event handling, listener behavior.

**TestBench e2e tests** drive a real browser. They are slower but verify the full stack — HTML rendering, CSS, web component behavior, and browser APIs like scroll position. Some behaviors (smooth scrolling, focus, hover states) can only be tested this way.

Both approaches use page objects with the same API, so the shared test cases are structurally identical — compare `MainViewTest` and `MainViewIT` side by side to see this clearly.
