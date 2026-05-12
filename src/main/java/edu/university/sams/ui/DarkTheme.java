package edu.university.sams.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;

public final class DarkTheme {
    private DarkTheme() {}

    public static void apply() {
        // Light monochrome palette
        Color white = Color.WHITE;
        Color black = Color.BLACK;
        Color gray = new Color(200, 200, 200);

        // Global defaults
        UIManager.put("control", white);
        UIManager.put("info", white);
        UIManager.put("nimbusBase", white);
        UIManager.put("nimbusBlueGrey", white);
        UIManager.put("nimbusLightBackground", white);
        UIManager.put("text", black);

        // Panels and backgrounds
        UIManager.put("Panel.background", white);
        UIManager.put("OptionPane.background", white);
        UIManager.put("ScrollPane.background", white);
        UIManager.put("Viewport.background", white);
        UIManager.put("TabbedPane.contentAreaColor", white);
        UIManager.put("TabbedPane.background", white);
        UIManager.put("TabbedPane.darkShadow", gray);

        // Labels and text
        UIManager.put("Label.foreground", black);
        UIManager.put("TitledBorder.titleColor", black);
        UIManager.put("ToolTip.background", white);
        UIManager.put("ToolTip.foreground", black);

        // Buttons (white background, black text)
        UIManager.put("Button.background", white);
        UIManager.put("Button.foreground", black);
        UIManager.put("Button.disabledText", black);
        UIManager.put("Button.select", white);
        UIManager.put("Button.focus", black);
        UIManager.put("Button.borderColor", black);

        // Inputs
        UIManager.put("TextField.background", white);
        UIManager.put("TextField.foreground", black);
        UIManager.put("TextField.caretForeground", black);
        UIManager.put("TextField.inactiveForeground", black);
        UIManager.put("PasswordField.background", white);
        UIManager.put("PasswordField.foreground", black);
        UIManager.put("PasswordField.caretForeground", black);
        UIManager.put("PasswordField.inactiveForeground", black);
        UIManager.put("ComboBox.background", white);
        UIManager.put("ComboBox.foreground", black);
        UIManager.put("ComboBox.selectionBackground", gray);
        UIManager.put("ComboBox.selectionForeground", black);

        // Tables
        UIManager.put("Table.background", white);
        UIManager.put("Table.foreground", black);
        UIManager.put("Table.gridColor", gray);
        UIManager.put("Table.selectionBackground", gray);
        UIManager.put("Table.selectionForeground", black);
        UIManager.put("TableHeader.background", white);
        UIManager.put("TableHeader.foreground", black);

        // Lists
        UIManager.put("List.background", white);
        UIManager.put("List.foreground", black);
        UIManager.put("List.selectionBackground", gray);
        UIManager.put("List.selectionForeground", black);

        // Progress/Slider
        UIManager.put("ProgressBar.background", white);
        UIManager.put("ProgressBar.foreground", black);

        // Menus
        UIManager.put("Menu.background", white);
        UIManager.put("Menu.foreground", black);
        UIManager.put("MenuItem.background", white);
        UIManager.put("MenuItem.foreground", black);
        UIManager.put("PopupMenu.background", white);

        // OptionPane
        UIManager.put("OptionPane.messageForeground", black);
        UIManager.put("OptionPane.foreground", black);

        // Default font
        Font base = new Font("Segoe UI", Font.PLAIN, 13);
        Enumeration<?> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof Font f) {
                UIManager.put(key, base.deriveFont(f.getStyle(), f.getSize2D()));
            }
        }
    }
}
