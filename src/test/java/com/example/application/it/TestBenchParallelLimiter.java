package com.example.application.it;

import org.jspecify.annotations.NonNull;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;

/**
 * Shims Failsafe's {@code <systemPropertyVariables>} for IDE runs: Maven injects
 * {@code com.vaadin.testbench.Parameters.testsInParallel} before the forked JVM
 * starts; this SPI listener does the same before JUnit's executor is configured.
 *
 * <p>TestBench's {@code ParallelConfigurationStrategy} captures the parallelism
 * limit once, during JUnit executor setup, before any test class is instantiated.
 * Setting it from a {@code @ExtendWith} extension is too late: JUnit loads
 * extensions at instantiation time, after the executor is already configured.
 * On IDE runs, the limit from {@code it-test.properties} would be silently
 * ignored without this class.
 *
 * <p>This listener is registered via SPI
 * ({@code META-INF/services/org.junit.platform.launcher.LauncherSessionListener})
 * and fires at {@code launcherSessionOpened} — before discovery and before
 * executor setup — giving it a chance to call
 * {@link ServerExtension#applyConcurrentLimit()} before
 * {@code ParallelConfigurationStrategy} is ever instantiated.
 *
 * <p>Fires in both Maven and IDE environments, but what happens next differs:
 * <ul>
 *   <li><b>Maven runs:</b> Failsafe has already set
 *       {@code com.vaadin.testbench.Parameters.testsInParallel} via
 *       {@code <systemPropertyVariables>} before the forked JVM starts, so
 *       {@link ServerExtension#applyConcurrentLimit()} finds the property
 *       present and returns immediately — this call is a no-op.</li>
 *   <li><b>IDE runs:</b> the property is not pre-set, so this call is the one
 *       that actually configures parallelism in time. The
 *       {@link ServerExtension} static initializer is retained as a safety net
 *       for IDEs that may not fire the {@code LauncherSessionListener} SPI.</li>
 * </ul>
 */
public class TestBenchParallelLimiter implements LauncherSessionListener {

    @Override
    public void launcherSessionOpened(@NonNull LauncherSession session) {
        ServerExtension.applyConcurrentLimit();
    }
}