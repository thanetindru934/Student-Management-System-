package edu.university.sams.ui;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class UiKit {
    private UiKit() {}

    // Nordic/Scandi dark palette
    public static final Color BG_DARK = new Color(30, 34, 40);
    public static final Color FG_LIGHT = new Color(240, 240, 240);
    public static final Color BORDER = new Color(70, 75, 85);
    public static final Color PRIMARY = new Color(94, 129, 172);   // Nord blue
    public static final Color SUCCESS = new Color(163, 190, 140);  // Nord green
    public static final Color WARNING = new Color(208, 135, 112);  // Nord orange
    public static final Color DANGER = new Color(191, 97, 106);    // Nord red
    public static final Color SECONDARY = new Color(67, 76, 94);   // Slate

    // Style helpers
    public static void stylePrimary(JButton b) { styleButton(b, PRIMARY); }
    public static void styleSecondary(JButton b) { styleButton(b, SECONDARY); }
    public static void styleSuccess(JButton b) { styleButton(b, SUCCESS); }
    public static void styleDanger(JButton b) { styleButton(b, DANGER); }
    public static void styleWarning(JButton b) { styleButton(b, WARNING); }

    private static void styleButton(JButton b, Color base) {
        b.setBackground(base);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new RoundedBorder(10, base.darker()));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        Insets m = b.getMargin();
        b.setMargin(new Insets(8, Math.max(12, m.left), 8, Math.max(12, m.right)));
        // Simple hover
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(base.brighter()); }
            @Override public void mouseExited(MouseEvent e) { b.setBackground(base); }
        });
    }

    // Rounded border for buttons/cards
    public static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color borderColor;
        public RoundedBorder(int radius, Color borderColor) {
            this.radius = radius; this.borderColor = borderColor;
        }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(borderColor);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
        @Override
        public Insets getBorderInsets(Component c) { return new Insets(6, 10, 6, 10); }
        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(6, 10, 6, 10); return insets;
        }
    }
}
