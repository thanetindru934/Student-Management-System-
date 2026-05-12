package edu.university.sams.ui;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class ShadcnUI {
    private ShadcnUI() {}

    public enum Variant { DEFAULT, SECONDARY, DESTRUCTIVE, OUTLINE, GHOST, LINK }
    public enum Size { SM, MD, LG }

    // Strict monochrome palette
    public static final Color BG = Color.BLACK;
    public static final Color FG = Color.WHITE;
    public static final Color MUTED = Color.WHITE;
    public static final Color CARD = Color.BLACK;
    public static final Color BORDER = new Color(80, 80, 80);
    public static final Color ACCENT = Color.BLACK;     // unify to black
    public static final Color SECONDARY = Color.BLACK;  // unify to black
    public static final Color DESTRUCT = Color.BLACK;   // unify to black

    public static class Button {
        public static void applyVariant(JButton b, Variant variant) {
            applyVariant(b, variant, Size.MD);
        }
        public static void applyVariant(JButton b, Variant variant, Size size) {
            b.setOpaque(true);
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setForeground(Color.WHITE);

            int padV = switch (size) { case SM -> 6; case MD -> 8; default -> 12; };
            int padH = switch (size) { case SM -> 10; case MD -> 14; default -> 18; };
            b.setBorder(new RoundedBorder(8, BORDER));
            b.setMargin(new Insets(padV, padH, padV, padH));

            Color base;
            Color hover;
            switch (variant) {
                case OUTLINE -> { base = new Color(0,0,0,0); hover = new Color(255,255,255,20); }
                case GHOST -> { base = new Color(0,0,0,0); hover = new Color(255,255,255,15); }
                case LINK -> {
                    base = new Color(0,0,0,0);
                    hover = new Color(0,0,0,0);
                    b.setForeground(Color.WHITE);
                    b.setBorder(BorderFactory.createEmptyBorder(padV, padH, padV, padH));
                    b.setContentAreaFilled(false);
                    return;
                }
                default -> { base = Color.BLACK; hover = new Color(40, 40, 40); }
            }

            b.setBackground(base);
            b.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { if (b.isEnabled()) b.setBackground(hover); }
                @Override public void mouseExited(MouseEvent e) { if (b.isEnabled()) b.setBackground(base); }
            });

            if (variant == Variant.OUTLINE) {
                b.setContentAreaFilled(false);
                b.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedBorder(8, BORDER),
                        BorderFactory.createEmptyBorder(padV, padH, padV, padH)
                ));
            }
        }
    }

    public static class Input {
        public static void apply(JComponent c) {
            c.setForeground(FG);
            c.setBackground(Color.BLACK);
            c.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(8, BORDER),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)
            ));
            c.addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) {
                    c.setBorder(BorderFactory.createCompoundBorder(
                            new RoundedBorder(8, ACCENT),
                            BorderFactory.createEmptyBorder(6, 10, 6, 10)
                    ));
                }
                @Override public void focusLost(FocusEvent e) {
                    c.setBorder(BorderFactory.createCompoundBorder(
                            new RoundedBorder(8, BORDER),
                            BorderFactory.createEmptyBorder(6, 10, 6, 10)
                    ));
                }
            });
        }
    }

    public static class Table {
        public static void apply(JTable table) {
            table.setBackground(Color.BLACK);
            table.setForeground(FG);
            table.setGridColor(BORDER);
            table.setSelectionBackground(new Color(40, 40, 40));
            table.setSelectionForeground(FG);
            table.setRowHeight(26);
            JTableHeader header = table.getTableHeader();
            header.setBackground(Color.BLACK);
            header.setForeground(FG);
            header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        }
    }

    public static class Card {
        public static JPanel wrap(JComponent inner) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(CARD);
            card.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(10, BORDER),
                    BorderFactory.createEmptyBorder(16, 16, 16, 16)
            ));
            card.add(inner, BorderLayout.CENTER);
            return card;
        }
    }

    // Rounded border used across components
    public static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;
        public RoundedBorder(int radius, Color color) { this.radius = radius; this.color = color; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(8, 8, 8, 8); }
        @Override public Insets getBorderInsets(Component c, Insets insets) { insets.set(8,8,8,8); return insets; }
    }
}
