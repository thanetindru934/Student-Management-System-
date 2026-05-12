package edu.university.sams.gui;

import edu.university.sams.model.User;
import edu.university.sams.model.Student;
import edu.university.sams.model.Instructor;
import edu.university.sams.service.SecurityService;
import edu.university.sams.model.enums.UserRole;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.logging.Logger;
import edu.university.sams.ui.ShadcnUI;

import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.util.prefs.Preferences;

public class LoginWindow extends JFrame {

    private static final Logger LOGGER = Logger.getLogger(LoginWindow.class.getName());

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<UserRole> roleComboBox;
    private JButton loginButton;
    private JLabel statusLabel;

    // Keep reference to the left image panel for shortcuts
    private ImagePanel leftImagePanel;

    private SecurityService securityService;

    public LoginWindow() {
        this.securityService = new SecurityService();

        setTitle("SAMS - Student Attendance Management System");
        setSize(960, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        initComponents();
        setupLayout();
        setupEventHandlers();
        setupShortcuts();

        usernameField.requestFocus();
    }

    private void initComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        roleComboBox = new JComboBox<>(UserRole.values());
        loginButton = new JButton("Login");
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.WHITE);
        // shadcn/ui input styling
        ShadcnUI.Input.apply(usernameField);
        ShadcnUI.Input.apply(passwordField);
        ShadcnUI.Input.apply(roleComboBox);

        ShadcnUI.Button.applyVariant(loginButton, ShadcnUI.Variant.DEFAULT, ShadcnUI.Size.LG);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Right side: form + credentials
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        // Lock sizes to avoid layout jumps when role selection changes
        Dimension fieldSize = new Dimension(360, 44);
        usernameField.setPreferredSize(fieldSize);
        usernameField.setMinimumSize(fieldSize);
        usernameField.setMaximumSize(fieldSize);
        passwordField.setPreferredSize(fieldSize);
        passwordField.setMinimumSize(fieldSize);
        passwordField.setMaximumSize(fieldSize);

        // Keep role dropdown width constant based on the longest item
        Dimension roleSize = new Dimension(160, 36);
        roleComboBox.setPrototypeDisplayValue(UserRole.ADMINISTRATOR);
        roleComboBox.setPreferredSize(roleSize);
        roleComboBox.setMinimumSize(roleSize);
        roleComboBox.setMaximumSize(roleSize);
        roleComboBox.setMaximumRowCount(UserRole.values().length);
        roleComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setHorizontalAlignment(LEFT);
                setBackground(isSelected ? Color.LIGHT_GRAY : Color.WHITE);
                setForeground(Color.BLACK);
                return c;
            }
        });

        loginButton.setPreferredSize(new Dimension(220, 40));
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setForeground(Color.BLACK);
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameField.setBackground(Color.WHITE);
        usernameField.setForeground(Color.BLACK);
        usernameField.setCaretColor(Color.BLACK);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setForeground(Color.BLACK);
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setBackground(Color.WHITE);
        passwordField.setForeground(Color.BLACK);
        passwordField.setCaretColor(Color.BLACK);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        roleLabel.setForeground(Color.BLACK);
        formPanel.add(roleLabel, gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        roleComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        roleComboBox.setBackground(Color.WHITE);
        roleComboBox.setForeground(Color.BLACK);
        roleComboBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        formPanel.add(roleComboBox, gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.CENTER;
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        formPanel.add(statusLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 10, 10, 10);
        formPanel.add(loginButton, gbc);

        JPanel credentialsPanel = new JPanel();
        credentialsPanel.setBackground(Color.WHITE);
        credentialsPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel credentialsLabel = new JLabel("<html><b>Default Credentials:</b><br>" +
                "Admin: admin / password<br>" +
                "Instructor: john.doe / password<br>" +
                "Student: jane.smith / password</html>");
        credentialsLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        credentialsLabel.setForeground(Color.GREEN);
        credentialsPanel.add(credentialsLabel);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(formPanel, BorderLayout.CENTER);
        rightPanel.add(credentialsPanel, BorderLayout.SOUTH);

        // Left side: hero image
        leftImagePanel = createLeftHeroPanel();

        // Two-column layout using JSplitPane (Image left, Form right) with a white divider
        leftImagePanel.setPreferredSize(new Dimension(520, getHeight()));
        rightPanel.setPreferredSize(new Dimension(520, getHeight()));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftImagePanel, rightPanel);
        split.setResizeWeight(0.5);
        split.setDividerSize(2);
        split.setBorder(BorderFactory.createEmptyBorder());
        split.setContinuousLayout(true);
        split.setBackground(Color.WHITE);
        split.setOpaque(true);
        split.setUI(new BasicSplitPaneUI() {
            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    @Override public void setBorder(Border b) { /* no border */ }
                    @Override public void paint(Graphics g) {
                        g.setColor(Color.WHITE);
                        g.fillRect(0, 0, getWidth(), getHeight());
                    }
                };
            }
        });

        add(split, BorderLayout.CENTER);

        // Set initial divider after layout
        SwingUtilities.invokeLater(() -> split.setDividerLocation(0.5));
    }

    // Left panel: full-bleed image (cover) using bundled/local asset
    private ImagePanel createLeftHeroPanel() {
        // Use local file name; ImagePanel will try classpath, Downloads, and fallbacks
        return new ImagePanel("zhanhui-li-1iuxWsIZ6ko-unsplash.jpg");
    }

    private void setupEventHandlers() {
        loginButton.addActionListener(this::performLogin);

        getRootPane().setDefaultButton(loginButton);
        usernameField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(this::performLogin);

        usernameField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                statusLabel.setText(" ");
            }
        });

        passwordField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                statusLabel.setText(" ");
            }
        });
    }

    private void performLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty()) {
            showError("Please enter username");
            usernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showError("Please enter password");
            passwordField.requestFocus();
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");
        statusLabel.setText("Authenticating...");
        statusLabel.setForeground(Color.BLUE);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<User, Void> loginWorker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                Thread.sleep(500);
                return securityService.authenticateUser(username, password, (UserRole) roleComboBox.getSelectedItem());
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                loginButton.setEnabled(true);
                loginButton.setText("Login");

                try {
                    User user = get();
                    if (user != null) {
                        LOGGER.info("Login successful for user: " + username);
                        // Start a session (UT-SEC-002 support)
                        try { edu.university.sams.security.SessionManager.startSession(user.getUserId()); } catch (Exception ignore) {}
                        boolean opened = openDashboard(user);
                        if (opened) {
                            dispose();
                        }
                    } else {
                        showError("Invalid username or password");
                        passwordField.setText("");
                        passwordField.requestFocus();
                    }
                } catch (Exception ex) {
                    LOGGER.severe("Login error: " + ex.getMessage());
                    showError("Login failed: " + ex.getMessage());
                }
            }
        };

        loginWorker.execute();
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(Color.RED);
    }

    private void setupShortcuts() {
        javax.swing.KeyStroke ks = javax.swing.KeyStroke.getKeyStroke("control I");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, "changeImage");
        getRootPane().getActionMap().put("changeImage", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (leftImagePanel != null) {
                    leftImagePanel.promptForImage();
                }
            }
        });
    }

    private boolean openDashboard(User user) {
        try {
            switch (user.getRole()) {
                case STUDENT -> new StudentDashboard((Student) user).setVisible(true);
                case INSTRUCTOR -> new InstructorDashboard((Instructor) user).setVisible(true);
                case ADMINISTRATOR -> new AdminDashboard(user).setVisible(true);
                default -> {
                    showError("Unknown user role");
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            LOGGER.severe("Error opening dashboard: " + e.getMessage());
            showError("Error opening dashboard");
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            edu.university.sams.ui.DarkTheme.apply();

            new LoginWindow().setVisible(true);
        });
    }
    // Helper panel to paint an image like CSS background-size: cover
    private static class ImagePanel extends JPanel {
        private java.awt.image.BufferedImage img;

        ImagePanel(String name) {
            setBackground(Color.WHITE);
            img = loadImageRobust(name);
            if (img == null) {
                System.err.println("[LoginWindow] Unable to load image: " + name + " (showing white panel)");
            }
            setToolTipText("Right-click or double-click to change image (or press Ctrl+I)");
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (javax.swing.SwingUtilities.isRightMouseButton(e) || e.getClickCount() == 2) {
                        promptForImage();
                    }
                }
            });
        }

        private static java.awt.image.BufferedImage loadImageRobust(String name) {
            // 0) System property override
            try {
                String prop = System.getProperty("sams.login.image");
                if (prop != null && !prop.isBlank()) {
                    java.io.File f = new java.io.File(prop.trim());
                    if (f.exists() && f.isFile()) {
                        System.out.println("[LoginWindow] Loaded image from system property: " + f.getAbsolutePath());
                        return javax.imageio.ImageIO.read(f);
                    }
                }
            } catch (Exception ignored) {}

            // 0.1) Saved user preference (remembered after first manual selection)
            try {
                Preferences prefs = Preferences.userNodeForPackage(LoginWindow.class);
                String saved = prefs.get("login.image.path", null);
                if (saved != null && !saved.isBlank()) {
                    java.io.File f = new java.io.File(saved);
                    if (f.exists() && f.isFile()) {
                        System.out.println("[LoginWindow] Loaded image from saved preference: " + f.getAbsolutePath());
                        return javax.imageio.ImageIO.read(f);
                    }
                }
            } catch (Exception ignored) {}

            // 1) Classpath: with and without leading slash
            try {
                java.net.URL url = LoginWindow.class.getResource(name.startsWith("/") ? name : "/" + name);
                if (url != null) {
                    System.out.println("[LoginWindow] Loaded image from classpath: " + url);
                    return javax.imageio.ImageIO.read(url);
                }
            } catch (Exception ignored) {}
            try {
                java.net.URL url = LoginWindow.class.getResource(name);
                if (url != null) {
                    System.out.println("[LoginWindow] Loaded image from classpath (relative): " + url);
                    return javax.imageio.ImageIO.read(url);
                }
            } catch (Exception ignored) {}

            // 2) Try user's Downloads folder explicitly (Windows/macOS/Linux + OneDrive)
            try {
                String home = System.getProperty("user.home");
                if (home != null && !home.isBlank()) {
                    String[] downloads = new String[] {
                            home + "/Downloads/" + name,
                            home + "\\Downloads\\" + name,
                            home + "/OneDrive/Downloads/" + name,
                            home + "\\OneDrive\\Downloads\\" + name
                    };
                    for (String p : downloads) {
                        java.io.File f = new java.io.File(p);
                        if (f.exists() && f.isFile()) {
                            System.out.println("[LoginWindow] Loaded image from Downloads: " + f.getAbsolutePath());
                            return javax.imageio.ImageIO.read(f);
                        }
                    }
                }
            } catch (Exception ignored) {}

            // 3) Common project folders relative to working dir
            String[] roots = new String[] {
                    ".", "SAMS", "src/main/resources", "SAMS/src/main/resources",
                    "src/main/java", "SAMS/src/main/java", "resources", "assets", ""
            };
            for (String root : roots) {
                try {
                    java.io.File f = root.isEmpty() ? new java.io.File(name) : new java.io.File(root, name);
                    if (f.exists() && f.isFile()) {
                        System.out.println("[LoginWindow] Loaded image from file: " + f.getAbsolutePath());
                        return javax.imageio.ImageIO.read(f);
                    }
                } catch (Exception ignored) {}
            }

            // 4) Ask the user once (remember selection)
            try {
                final java.awt.image.BufferedImage[] out = new java.awt.image.BufferedImage[1];
                final String[] chosenPath = new String[1];
                Runnable chooser = () -> {
                    JFileChooser fc = new JFileChooser(new java.io.File(System.getProperty("user.home"), "Downloads"));
                    fc.setDialogTitle("Select Login Image");
                    int res = fc.showOpenDialog(null);
                    if (res == JFileChooser.APPROVE_OPTION) {
                        java.io.File sel = fc.getSelectedFile();
                        try {
                            out[0] = javax.imageio.ImageIO.read(sel);
                            chosenPath[0] = sel.getAbsolutePath();
                        } catch (Exception ex) {
                            out[0] = null;
                        }
                    }
                };
                if (SwingUtilities.isEventDispatchThread()) {
                    chooser.run();
                } else {
                    try {
                        SwingUtilities.invokeAndWait(chooser);
                    } catch (Exception ignored) {}
                }
                if (out[0] != null && chosenPath[0] != null) {
                    try {
                        Preferences.userNodeForPackage(LoginWindow.class)
                                   .put("login.image.path", chosenPath[0]);
                    } catch (Exception ignored) {}
                    System.out.println("[LoginWindow] Loaded image via chooser: " + chosenPath[0]);
                    return out[0];
                }
            } catch (Exception ignored) {}

            // 5) Last-resort web fallback (public Unsplash image)
            try {
                String fallback = "https://images.unsplash.com/photo-1523050854058-8df90110c9f1?auto=format&fit=crop&w=1400&q=80";
                System.out.println("[LoginWindow] Using web fallback image.");
                return javax.imageio.ImageIO.read(new java.net.URL(fallback));
            } catch (Exception ignored) {}

            return null;
        }

        void promptForImage() {
            javax.swing.JFileChooser fc = new javax.swing.JFileChooser(
                    new java.io.File(System.getProperty("user.home"), "Downloads"));
            fc.setDialogTitle("Select Login Image");
            int res = fc.showOpenDialog(this);
            if (res == javax.swing.JFileChooser.APPROVE_OPTION) {
                java.io.File sel = fc.getSelectedFile();
                try {
                    java.awt.image.BufferedImage newImg = javax.imageio.ImageIO.read(sel);
                    if (newImg != null) {
                        this.img = newImg;
                        try {
                            java.util.prefs.Preferences.userNodeForPackage(LoginWindow.class)
                                    .put("login.image.path", sel.getAbsolutePath());
                        } catch (Exception ignored) {}
                        repaint();
                        System.out.println("[LoginWindow] Loaded image via chooser: " + sel.getAbsolutePath());
                    }
                } catch (Exception ex) {
                    System.err.println("[LoginWindow] Failed to load chosen image: " + ex.getMessage());
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img == null) return;
            int pw = getWidth(), ph = getHeight();
            int iw = img.getWidth(), ih = img.getHeight();
            if (pw <= 0 || ph <= 0 || iw <= 0 || ih <= 0) return;

            double scale = Math.max(pw / (double) iw, ph / (double) ih);
            int sw = (int) Math.ceil(iw * scale);
            int sh = (int) Math.ceil(ih * scale);
            int x = (pw - sw) / 2;
            int y = (ph - sh) / 2;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(img, x, y, sw, sh, null);
            g2.dispose();
        }
    }
}