package com.example.application.it;

import com.vaadin.testbench.Parameters;
import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import org.eclipse.jetty.util.resource.ResourceFactory;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URI;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * Shims the Maven build lifecycle for IDE runs: {@code mvn verify -Pit} starts
 * and stops Jetty around the IT tests automatically via the Jetty Maven plugin;
 * this extension does the same when no server is detected on the configured
 * port. Reference-counted across concurrent test classes so only the first class
 * starts it and only the last stops it.
 *
 * <p>When tests are run via {@code mvn verify -Pit}, Maven starts Jetty before
 * Failsafe and sets the {@code deployment.port} system property — this extension
 * detects the occupied port and is a no-op. When tests are run directly from an
 * IDE, the extension reads the port from {@code it-test.properties} (a
 * Maven-filtered resource whose values come from the {@code it-deployment.port}
 * POM property), sets the system property, and starts the server itself.
 *
 * <p>Server startup details:
 * <ul>
 *   <li><b>Port detection:</b> probes the configured port with a {@code Socket}
 *       connection before doing anything; skips startup if already listening.</li>
 *   <li><b>Classpath bridging:</b> Jetty's annotation scanner needs the full
 *       application classpath to discover Vaadin's
 *       {@code LookupServletContainerInitializer}. In IntelliJ,
 *       {@code java.class.path} has everything, but in a Maven exec context it
 *       contains only {@code plexus-classworlds} — the actual project JARs live
 *       in the Plexus {@code URLClassLoader}. This extension walks the
 *       thread-context classloader hierarchy and adds any {@code URLClassLoader}
 *       entries it finds. Without this, {@code VaadinServlet} is never registered
 *       and the app returns 404.</li>
 *   <li><b>Readiness detection:</b> after {@code server.start()}, polls the root
 *       URL for a {@code type="module"} script tag — present in Vaadin's HTML
 *       shell but absent from a bare Jetty startup page — before signaling
 *       readiness to the waiting test classes.</li>
 * </ul>
 */
public class ServerExtension implements BeforeAllCallback, AfterAllCallback {

    static {
        applyConcurrentLimit();
    }

    /**
     * Sets {@code com.vaadin.testbench.Parameters.testsInParallel} from
     * {@code it-test.properties} so that TestBench's
     * {@code ParallelConfigurationStrategy} reads the configured value when it
     * captures {@code Parameters.getTestsInParallel()} during executor setup.
     *
     * <p>Called from two places: this class's static initializer (loaded when
     * JUnit instantiates the extension), and {@link TestBenchParallelLimiter}
     * (fired at JUnit launcher-session open via SPI). Both call this method in
     * every environment, but only one call ever does meaningful work:
     * <ul>
     *   <li><b>Maven runs:</b> Failsafe already set the system property via
     *       {@code <systemPropertyVariables>} before the forked JVM started, so
     *       this method finds the property present and returns immediately —
     *       both the SPI call and the static-initializer call are no-ops.</li>
     *   <li><b>IDE runs:</b> The SPI call from {@code TestBenchParallelLimiter}
     *       fires first (at launcher-session open, before executor setup) and
     *       sets the property. The static-initializer call fires later and is
     *       then a no-op. The static initializer is retained as a safety net
     *       for IDEs that may not fire the {@code LauncherSessionListener}
     *       SPI.</li>
     * </ul>
     */
    static void applyConcurrentLimit() {
        if (!System.getProperties().containsKey("com.vaadin.testbench.Parameters.testsInParallel")) {
            int limit = readIntegrationTestConcurrentLimit();
            System.setProperty("com.vaadin.testbench.Parameters.testsInParallel", String.valueOf(limit));
            Parameters.setTestsInParallel(limit);
        }
    }

    private static int readIntegrationTestConcurrentLimit() {
        var props = new Properties();
        try (var in = ServerExtension.class.getClassLoader()
                .getResourceAsStream("it-test.properties")) {
            props.load(in);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to read integration-test.concurrent-limit from it-test.properties", e);
        }
        return Integer.parseInt(props.getProperty("integration-test.concurrent-limit"));
    }

    private static final Object LOCK = new Object();
    private static Server server;
    private static int useCount;
    private static CountDownLatch ready;

    @Override
    public void beforeAll(@NonNull ExtensionContext context) throws Exception {
        boolean isFirst;
        CountDownLatch signal;
        synchronized (LOCK) {
            useCount++;
            isFirst = useCount == 1;
            if (isFirst) {
                ensureDeploymentPortSet();
                ready = new CountDownLatch(1);
            }
            signal = ready;
        }
        // Polling for readiness runs outside the lock, so concurrent test
        // classes' beforeAll calls aren't held up by the 30-second wait.
        if (isFirst) {
            try {
                if (!isPortInUse(deploymentPort())) {
                    startServer(deploymentPort());
                }
            } finally {
                signal.countDown();
            }
        } else {
            signal.await();
        }
    }

    @Override
    public void afterAll(@NonNull ExtensionContext context) throws Exception {
        synchronized (LOCK) {
            useCount--;
            if (useCount == 0 && server != null) {
                server.stop();
                server = null;
            }
        }
    }


    // INTERNAL helpers

    private static void ensureDeploymentPortSet() throws Exception {
        if (System.getProperty("deployment.port") == null) {
            var props = new Properties();
            try (var in = ServerExtension.class.getClassLoader()
                    .getResourceAsStream("it-test.properties")) {
                props.load(in);
            }
            System.setProperty("deployment.port", props.getProperty("deployment.port"));
        }
    }

    private static int deploymentPort() {
        return Integer.parseInt(System.getProperty("deployment.port"));
    }

    private static String collectClasspath() {
        var entries = new LinkedHashSet<String>();
       Collections.addAll(entries, System.getProperty("java.class.path").split(File.pathSeparator));
        for (var cl = Thread.currentThread().getContextClassLoader(); cl != null; cl = cl.getParent()) {
            if (cl instanceof URLClassLoader urlCl) {
                for (var url : urlCl.getURLs()) {
                    try {
                        entries.add(new File(url.toURI()).getAbsolutePath());
                    } catch (Exception _) {
                        // skip non-file URLs (e.g. jrt:/, http:/)
                    }
                }
            }
        }
        return String.join(";", entries);
    }

    private static boolean isPortInUse(int port) {
        try (var _ = new Socket("localhost", port)) {
            return true;
        } catch (Exception _) {
            return false;
        }
    }

    private static void startServer(int port) throws Exception {
        server = new Server();
        var connector = new ServerConnector(server);
        connector.setPort(port);
        server.addConnector(connector);

        var webApp = new WebAppContext();
        webApp.setContextPath("/");
        webApp.setBaseResource(ResourceFactory.of(webApp)
                .newResource(new File("src/main/webapp").toPath()));
        // Jetty's AnnotationConfiguration scans JAR files to discover SCIs and
        // needs explicit file URLs because an arbitrary ClassLoader cannot be
        // enumerated. Combine java.class.path with URLs walked from the context
        // ClassLoader hierarchy: in IntelliJ the former has everything, but in
        // Maven (e.g., mvn exec:java) java.class.path only contains plexus-
        // classworlds — the actual project jars live in the Plexus URLClassLoader.
        // Without this, Vaadin's LookupServletContainerInitializer is never run
        // and ServletDeployer never registers VaadinServlet at "/*".
        webApp.setExtraClasspath(collectClasspath());
        webApp.setParentLoaderPriority(true);
        server.setHandler(webApp);
        server.start();
        waitUntilReady(port);
    }

    /**
     * Blocks until Vaadin is serving real content. Checks for a type="module"
     * script tag, which appears in Vaadin's HTML shell but not in a directory
     * listing or a bare Jetty startup page.
     */
    @SuppressWarnings({"BusyWait", "java:S2925"})
    private static void waitUntilReady(int port) throws Exception {
        var url = URI.create("http://localhost:" + port + "/").toURL();
        var deadline = System.currentTimeMillis() + 30_000;
        while (System.currentTimeMillis() < deadline) {
            try {
                var conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(1_000);
                conn.setReadTimeout(10_000);
                if (conn.getResponseCode() == 200) {
                    var body = new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                    if (body.contains("type=\"module\"")) {
                        return;
                    }
                }
            } catch (Exception _) {
                // connection refused — server is not yet ready
            }
            Thread.sleep(250);
        }
        throw new IllegalStateException(
                "Vaadin did not become ready on port " + port + " within 30 seconds");
    }
}
