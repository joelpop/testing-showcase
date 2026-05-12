# Testing Showcase

A Vaadin application demonstrating two complementary testing approaches: **browserless unit tests** and **TestBench end-to-end tests**, using page objects that mirror each other.

## The Application

A greeting app where users type a name and click **Say hello** (or press Enter). Each greeting appears as a card with a timestamp and a close button. Cards accumulate in a scrollable list, automatically scrolling the newest card into view.

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

Page objects extend `ComponentTester<T>` and expose the same high-level methods as their TestBench counterparts (`greet()`, `getCards()`, `getMessage()`, `close()`). Internally they use `find(ComponentType.class)` to locate child components in the server-side component tree, without relying on DOM structure.

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

#### E2E-Only Test

`greetButton_scrollsNewestCardIntoView` is only in `MainViewIT` — browserless tests have no concept of scroll position or viewport visibility. It adds enough cards to force scrolling, then verifies via JavaScript that the newest card is within the scroller's visible bounds.

---

## Similarities Between Both Approaches

| | Browserless (`MainViewTest`) | TestBench (`MainViewIT`) |
|---|---|---|
| Page object base | `ComponentTester<T>` | `TestBenchElement` |
| Method names | `greet()`, `getCards()`, `getMessage()`, `close()` | same |
| Test cases | 6 shared cases | 6 shared + 1 e2e-only |
| Element location | component tree (`find()`) | DOM (`$()`) |
| Run with | `mvn test` | `mvn verify -Pit` |
| Needs browser | no | yes (Chrome) |
| Needs server | no | yes (Jetty on 9090) |
| Speed | ~milliseconds | ~seconds |

## Key Differences

**Browserless tests** run entirely server-side. They are fast, reliable (no Selenium timing issues), and run in CI without a display. They verify server-side logic — component state, event handling, listener behavior.

**TestBench e2e tests** drive a real browser. They are slower but verify the full stack — HTML rendering, CSS, web component behavior, and browser APIs like scroll position. Some behaviors (smooth scrolling, focus, hover states) can only be tested this way.

Both approaches use page objects with the same API, so the shared test cases are structurally identical — compare `MainViewTest` and `MainViewIT` side by side to see this clearly.
