package com.example.application;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * Configures the application shell with Lumo theme & utility classes
 * and application stylesheet.
 */
@StyleSheet(Lumo.STYLESHEET)
@StyleSheet(Lumo.UTILITY_STYLESHEET)
@StyleSheet("styles.css")
public class AppShell implements AppShellConfigurator {
}
