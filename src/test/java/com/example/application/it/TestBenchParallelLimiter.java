package com.example.application.it;

import org.jspecify.annotations.NonNull;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;

/**
 * Sets the TestBench parallel test limit at JUnit launcher-session open, which
 * fires before {@code ParallelConfigurationStrategy} is instantiated.
 *
 * <p>This is the path that limits IDE runs (e.g., IntelliJ). The
 * {@link ServerExtension} static initializer is too late in that scenario
 * because {@code @ExtendWith} extension classes are not loaded during JUnit
 * discovery — only when JUnit instantiates them for a test, which is after the
 * parallel executor (and thus {@code ParallelConfigurationStrategy}) has
 * already been configured.
 *
 * <p>Registered via SPI in
 * {@code META-INF/services/org.junit.platform.launcher.LauncherSessionListener}.
 * Delegates to {@link ServerExtension#applyConcurrentLimit()} for the actual
 * work.
 */
public class TestBenchParallelLimiter implements LauncherSessionListener {

    @Override
    public void launcherSessionOpened(@NonNull LauncherSession session) {
        ServerExtension.applyConcurrentLimit();
    }
}