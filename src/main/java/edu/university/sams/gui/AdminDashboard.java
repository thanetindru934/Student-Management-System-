package edu.university.sams.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import edu.university.sams.dao.AttendanceDAOImpl;
import edu.university.sams.dao.UserDAOImpl;
import edu.university.sams.dao.CourseDAOImpl;
import edu.university.sams.dao.DatabaseManager;
import edu.university.sams.model.User;
import edu.university.sams.dialog.AddUserDialog;
import edu.university.sams.dialog.EditUserDialog;
import edu.university.sams.ui.UiKit;
import edu.university.sams.ui.ShadcnUI;

public class AdminDashboard extends JFrame {

    private JFrame parentFrame; // Main JFrame
    private AttendanceDAOImpl attendanceDAO;
    private UserDAOImpl userDAO;
    private CourseDAOImpl courseDAO;
    private User currentUser;
    private JTabbedPane tabbedPane;

    private JTable usersTable;
    private DefaultTableModel userTableModel;
    private TableRowSorter<DefaultTableModel> userSorter;

    // Live stat labels
    private JLabel lblTotalUsers, lblStudents, lblInstructors, lblTotalCourses;
    private JLabel lblTodaysSessions, lblAttendanceRate, lblSystemUptime, lblDatabaseSize;
    private long startMillis = System.currentTimeMillis();
    private javax.swing.Timer statsTimer;

    private static final Color BRAND = Color.WHITE;
    private static final Color ACCENT = Color.WHITE;
    private static final Color DANGER = Color.WHITE;
    private static final Color WARNING = Color.WHITE;
    private static final Color PURPLE = Color.WHITE;

    public AdminDashboard(User currentUser) {
        super("SAMS - Admin Dashboard");
        this.parentFrame = this;
        this.attendanceDAO = new AttendanceDAOImpl(edu.university.sams.dao.DatabaseManager.getInstance());
        this.userDAO = new UserDAOImpl(edu.university.sams.dao.DatabaseManager.getInstance());
        this.courseDAO = new CourseDAOImpl(edu.university.sams.dao.DatabaseManager.getInstance());
        this.currentUser = currentUser;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1280, 840);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 700));
        initializeGUI();
    }

    private void initializeGUI() {
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Dashboard", createDashboardPanel());
        tabbedPane.addTab("User Management", createUserManagementPanel());
        tabbedPane.addTab("Course Management", createCourseManagementPanel());
        tabbedPane.addTab("Reports", createReportsPanel());
        tabbedPane.addTab("System Settings", createSystemSettingsPanel());

        add(tabbedPane, BorderLayout.CENTER);
        // Kick off initial and periodic stats refresh
        setupStatsRefresh();
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.BLACK);
        header.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        JLabel title = new JLabel("Administrator Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        JLabel welcome = new JLabel("Welcome, " + currentUser.getFirstName() + " " + currentUser.getLastName());
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcome.setForeground(new Color(230, 240, 255));
        left.add(title);
        left.add(welcome);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        JButton refresh = new JButton("↻ Refresh");
        JButton logout = new JButton("⎋ Logout");
        stylePrimary(refresh);
        styleDanger(logout);
        refresh.addActionListener(e -> refreshAll());
        logout.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginWindow().setVisible(true));
        });
        right.add(refresh);
        right.add(logout);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    // -------------------- DASHBOARD --------------------
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel stats = new JPanel(new GridLayout(2, 4, 12, 12));

        lblTotalUsers = new JLabel("…", SwingConstants.CENTER);
        lblStudents = new JLabel("…", SwingConstants.CENTER);
        lblInstructors = new JLabel("…", SwingConstants.CENTER);
        lblTotalCourses = new JLabel("…", SwingConstants.CENTER);
        lblTodaysSessions = new JLabel("…", SwingConstants.CENTER);
        lblAttendanceRate = new JLabel("…", SwingConstants.CENTER);
        lblSystemUptime = new JLabel("…", SwingConstants.CENTER);
        lblDatabaseSize = new JLabel("…", SwingConstants.CENTER);

        stats.add(createStatCard("Total Users", lblTotalUsers, BRAND));
        stats.add(createStatCard("Students", lblStudents, ACCENT));
        stats.add(createStatCard("Instructors", lblInstructors, WARNING));
        stats.add(createStatCard("Total Courses", lblTotalCourses, PURPLE));
        stats.add(createStatCard("Today's Sessions", lblTodaysSessions, DANGER));
        stats.add(createStatCard("Attendance Rate", lblAttendanceRate, new Color(52, 73, 94)));
        stats.add(createStatCard("System Uptime", lblSystemUptime, new Color(149, 165, 166)));
        stats.add(createStatCard("Database Size", lblDatabaseSize, new Color(127, 140, 141)));

        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBorder(BorderFactory.createTitledBorder("Recent Activity"));
        String[] cols = {"Time", "User", "Action", "Details"};
        JTable logTable = new JTable(new DefaultTableModel(cols, 0));
        logTable.setRowHeight(24);
        activityPanel.add(new JScrollPane(logTable), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, stats, activityPanel);
        split.setResizeWeight(0.35);
        split.setDividerLocation(280);

        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    // -------------------- USER MANAGEMENT --------------------
    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Top bar: search + actions
        JPanel topBar = new JPanel(new BorderLayout(8, 8));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton addBtn = new JButton("＋ Add");
        JButton editBtn = new JButton("✎ Edit");
        JButton deleteBtn = new JButton("🗑 Deactivate");
        JButton refreshBtn = new JButton("↻ Refresh");
        stylePrimary(addBtn);
        styleSecondary(editBtn);
        styleDanger(deleteBtn);
        styleSecondary(refreshBtn);

        actions.add(addBtn);
        actions.add(editBtn);
        actions.add(deleteBtn);
        actions.add(refreshBtn);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JTextField searchField = new JTextField(24);
        searchField.putClientProperty("JTextField.placeholderText", "Search user...");
        ShadcnUI.Input.apply(searchField);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);

        topBar.add(actions, BorderLayout.WEST);
        topBar.add(searchPanel, BorderLayout.EAST);

        // Users table
        String[] userColumns = {"ID", "Username", "Name", "Email", "Role", "Status"};
        userTableModel = new DefaultTableModel(userColumns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        usersTable = new JTable(userTableModel);
        usersTable.setRowHeight(24);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userSorter = new TableRowSorter<>(userTableModel);
        usersTable.setRowSorter(userSorter);
        ShadcnUI.Table.apply(usersTable);

        JScrollPane tableScroll = new JScrollPane(usersTable);

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER);

        // Wire up actions
        addBtn.addActionListener(e -> showAddUserDialog());
        editBtn.addActionListener(e -> editSelectedUser(usersTable));
        deleteBtn.addActionListener(e -> deleteSelectedUser(usersTable));
        refreshBtn.addActionListener(e -> loadUsers());

        // Search filter
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void filter() {
                String text = searchField.getText().trim();
                if (text.isEmpty()) {
                    userSorter.setRowFilter(null);
                } else {
                    userSorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
                }
            }
            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) { filter(); }
        });

        loadUsers(); // initial load
        return panel;
    }

    private void loadUsers() {
        userTableModel.setRowCount(0);
        boolean filled = false;
        try {
            List<User> users = userDAO.getAllUsers();
            if (users != null && !users.isEmpty()) {
                for (User u : users) {
                    String fullName = (u.getFirstName() == null ? "" : u.getFirstName()) + " " +
                                      (u.getLastName() == null ? "" : u.getLastName());
                    String status = u.isActive() ? "Active" : "Inactive";
                    userTableModel.addRow(new Object[]{
                            u.getUserId(), u.getUsername(), fullName.trim(), u.getEmail(), u.getRole(), status
                    });
                }
                filled = true;
            }
        } catch (Exception ignore) {
            // Fall through to DB fallback
        }
        if (!filled) {
            loadUsersFromDb();
        }
        userTableModel.fireTableDataChanged();
    }

    private void loadUsersFromDb() {
        try {
            Connection conn = DatabaseManager.getInstance().getConnection();
            String sql = "SELECT user_id, username, first_name, last_name, email, role, is_active " +
                         "FROM users ORDER BY updated_at DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("user_id");
                    String username = rs.getString("username");
                    String first = rs.getString("first_name");
                    String last = rs.getString("last_name");
                    String email = rs.getString("email");
                    String role = rs.getString("role");
                    boolean active = rs.getBoolean("is_active");
                    String fullName = ((first == null ? "" : first) + " " + (last == null ? "" : last)).trim();
                    userTableModel.addRow(new Object[]{id, username, fullName, email, role, active ? "Active" : "Inactive"});
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load users from DB: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddUserDialog() {
        AddUserDialog dialog = new AddUserDialog(this, userDAO);
        dialog.setVisible(true);
        if (dialog.isUserAdded()) {
            JOptionPane.showMessageDialog(this, "User added successfully.");
            loadUsers();
        }
    }

    private void editSelectedUser(JTable usersTable) {
        int viewRow = usersTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.");
            return;
        }
        int modelRow = usersTable.convertRowIndexToModel(viewRow);
        String userId = usersTable.getModel().getValueAt(modelRow, 0).toString();
        User user = userDAO.findById(userId);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Selected user not found.");
            return;
        }
        EditUserDialog dialog = new EditUserDialog(this, userDAO, user);
        dialog.setVisible(true);
        if (dialog.isUserUpdated()) {
            JOptionPane.showMessageDialog(this, "User updated successfully.");
            loadUsers();
        }
    }

    private void deleteSelectedUser(JTable usersTable) {
        int viewRow = usersTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to deactivate.");
            return;
        }
        int modelRow = usersTable.convertRowIndexToModel(viewRow);
        String userId = usersTable.getModel().getValueAt(modelRow, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Deactivate this user? They will no longer be able to log in.",
                "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                userDAO.setUserActive(userId, false); // soft delete / deactivate
                JOptionPane.showMessageDialog(this, "User deactivated.");
                loadUsers();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to deactivate user: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // -------------------- COURSE MANAGEMENT --------------------
    private JPanel createCourseManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Top bar: actions + search
        JPanel topBar = new JPanel(new BorderLayout(8, 8));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton newBtn = new JButton("＋ New Course");
        JButton editBtn = new JButton("✎ Edit");
        JButton deleteBtn = new JButton("🗑 Delete");
        JButton refreshBtn = new JButton("↻ Refresh");
        JButton exportBtn = new JButton("⤓ Export CSV");
        stylePrimary(newBtn);
        styleSecondary(editBtn);
        styleDanger(deleteBtn);
        styleSecondary(refreshBtn);
        styleSecondary(exportBtn);
        actions.add(newBtn);
        actions.add(editBtn);
        actions.add(deleteBtn);
        actions.add(refreshBtn);
        actions.add(exportBtn);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JTextField searchField = new JTextField(24);
        ShadcnUI.Input.apply(searchField);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        topBar.add(actions, BorderLayout.WEST);
        topBar.add(searchPanel, BorderLayout.EAST);

        // Courses table
        String[] cols = {"ID", "Code", "Name", "Credits", "Semester", "Instructor"};
        DefaultTableModel courseModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable courseTable = new JTable(courseModel);
        courseTable.setRowHeight(24);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(courseModel);
        courseTable.setRowSorter(sorter);
        ShadcnUI.Table.apply(courseTable);

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void filter() {
                String text = searchField.getText().trim();
                sorter.setRowFilter(text.isEmpty() ? null :
                        RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
            }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
        });

        JScrollPane scroll = new JScrollPane(courseTable);

        // Load courses using reflection to avoid compile-time dependency
        Runnable loadCourses = () -> {
            courseModel.setRowCount(0);
            try {
                java.lang.reflect.Method m = courseDAO.getClass().getMethod("getAllCourses");
                Object list = m.invoke(courseDAO);
                if (list instanceof java.util.List<?> courses) {
                    for (Object c : courses) {
                        String id = invokeStr(c, "getCourseId");
                        String code = invokeStr(c, "getCourseCode");
                        String name = invokeStr(c, "getCourseName");
                        String credits = invokeStr(c, "getCredits");
                        String semester = invokeStr(c, "getSemester");
                        String instructor = nonEmpty(invokeStr(c, "getInstructorName"),
                                                     invokeStr(c, "getInstructorId"));
                        courseModel.addRow(new Object[]{id, code, name, credits, semester, instructor});
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Unable to load courses (DAO method missing).",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        };
        loadCourses.run();

        // Export courses CSV
        exportBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new java.io.File("courses_report.csv"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try (FileWriter out = new FileWriter(chooser.getSelectedFile())) {
                    out.write("ID,Code,Name,Credits,Semester,Instructor\n");
                    for (int i = 0; i < courseModel.getRowCount(); i++) {
                        out.write(String.format("%s,%s,%s,%s,%s,%s%n",
                                safeStr(courseModel.getValueAt(i,0)),
                                safeStr(courseModel.getValueAt(i,1)),
                                safeStr(courseModel.getValueAt(i,2)),
                                safeStr(courseModel.getValueAt(i,3)),
                                safeStr(courseModel.getValueAt(i,4)),
                                safeStr(courseModel.getValueAt(i,5))));
                    }
                    JOptionPane.showMessageDialog(this, "Exported successfully.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Actions (New/Edit/Delete) show friendly placeholders, as data-layer is unknown
        newBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Course creation not available in this build.", "Info", JOptionPane.INFORMATION_MESSAGE));
        editBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Course editing not available in this build.", "Info", JOptionPane.INFORMATION_MESSAGE));
        deleteBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Course deletion not available in this build.", "Info", JOptionPane.INFORMATION_MESSAGE));
        refreshBtn.addActionListener(e -> loadCourses.run());

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // -------------------- REPORTS --------------------
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton exportUsers = new JButton("⤓ Export Users CSV");
        JButton exportCourses = new JButton("⤓ Export Courses CSV");
        JButton exportSummary = new JButton("⤓ Export Summary TXT");
        styleSecondary(exportUsers);
        styleSecondary(exportCourses);
        styleSecondary(exportSummary);
        actions.add(exportUsers);
        actions.add(exportCourses);
        actions.add(exportSummary);

        JTextArea info = new JTextArea(
                "Reports Center:\n" +
                "- Export Users to CSV\n" +
                "- Export Courses to CSV (if available)\n" +
                "- Export a simple summary text report\n");
        info.setEditable(false);
        info.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        info.setBorder(new EmptyBorder(8, 8, 8, 8));

        panel.add(actions, BorderLayout.NORTH);
        panel.add(new JScrollPane(info), BorderLayout.CENTER);

        exportUsers.addActionListener(e -> exportUsersCsv());
        exportCourses.addActionListener(e -> exportCoursesCsv());
        exportSummary.addActionListener(e -> exportSummaryTxt());
        return panel;
    }

    private void exportUsersCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Users Report");
        chooser.setSelectedFile(new java.io.File("users_report.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter writer = new FileWriter(chooser.getSelectedFile())) {
                writer.write("ID,Username,First Name,Last Name,Email,Role,Active\n");
                List<User> users = userDAO.getAllUsers();
                for (User u : users) {
                    writer.write(String.format("%s,%s,%s,%s,%s,%s,%s%n",
                            safe(u.getUserId()), safe(u.getUsername()), safe(u.getFirstName()),
                            safe(u.getLastName()), safe(u.getEmail()), safe(String.valueOf(u.getRole())),
                            u.isActive() ? "Yes" : "No"));
                }
                JOptionPane.showMessageDialog(this, "Exported successfully.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // -------------------- SYSTEM SETTINGS --------------------
    private JPanel createSystemSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel content = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0;

        JLabel themeLbl = new JLabel("Theme:");
        JButton reapplyDark = new JButton("✦ Reapply Dark Theme");
        styleSecondary(reapplyDark);
        content.add(themeLbl, gbc);
        gbc.gridx = 1;
        content.add(reapplyDark, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel dbLbl = new JLabel("Database:");
        JButton reconnect = new JButton("⟳ Reconnect");
        stylePrimary(reconnect);
        content.add(dbLbl, gbc);
        gbc.gridx = 1;
        content.add(reconnect, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel cacheLbl = new JLabel("Cache:");
        JButton clearCache = new JButton("✖ Clear (UI)");
        styleDanger(clearCache);
        content.add(cacheLbl, gbc);
        gbc.gridx = 1;
        content.add(clearCache, gbc);

        panel.add(content, BorderLayout.NORTH);

        reapplyDark.addActionListener(e -> {
            edu.university.sams.ui.DarkTheme.apply();
            SwingUtilities.updateComponentTreeUI(this);
        });

        reconnect.addActionListener(e -> {
            try {
                edu.university.sams.dao.DatabaseManager.getInstance().getConnection();
                JOptionPane.showMessageDialog(this, "Database connection OK.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database reconnect failed: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearCache.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "UI cache cleared (nothing persistent to clear)."));

        return panel;
    }

    // -------------------- Helpers --------------------
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                new EmptyBorder(12, 12, 12, 12)
        ));
        card.setBackground(Color.BLACK);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(color);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(90, 90, 90));

        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);
        return card;
    }

    // Overload for live-updating value labels
    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                new EmptyBorder(12, 12, 12, 12)
        ));
        card.setBackground(Color.BLACK);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(color);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(90, 90, 90));

        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);
        return card;
    }

    private void setupStatsRefresh() {
        // Immediate refresh
        refreshStats();
        // Periodic refresh every 60 seconds
        statsTimer = new javax.swing.Timer(60_000, e -> refreshStats());
        statsTimer.setRepeats(true);
        statsTimer.start();
    }

    private void refreshStats() {
        try {
            lblTotalUsers.setText(safeCount(() -> userDAO.getAllUsers().size()));
        } catch (Exception e) {
            lblTotalUsers.setText("0");
        }
        try {
            lblStudents.setText(safeCount(() -> userDAO.getAllStudents().size()));
        } catch (Exception e) {
            lblStudents.setText("0");
        }
        try {
            lblInstructors.setText(safeCount(() -> userDAO.getAllInstructors().size()));
        } catch (Exception e) {
            lblInstructors.setText("0");
        }

        // Total courses: prefer SQL, fallback to DAO reflection
        int courses = countTotalCourses();
        lblTotalCourses.setText(String.valueOf(courses));

        // Today's sessions via SQL (if table exists)
        int todaySessions = countTodaysSessions();
        lblTodaysSessions.setText(String.valueOf(todaySessions));

        // Attendance rate overall
        String rate = computeAttendanceRate();
        lblAttendanceRate.setText(rate);

        // Uptime
        long elapsed = System.currentTimeMillis() - startMillis;
        lblSystemUptime.setText(formatDuration(elapsed));

        // Database size
        String dbSize = queryDatabaseSizeMB();
        lblDatabaseSize.setText(dbSize);
    }

    private int countTotalCourses() {
        // Try direct SQL
        try {
            java.sql.Connection conn = edu.university.sams.dao.DatabaseManager.getInstance().getConnection();
            try (java.sql.PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM courses");
                 java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception ignored) { }
        // Fallback: reflection on DAO
        try {
            java.lang.reflect.Method m = courseDAO.getClass().getMethod("getAllCourses");
            Object list = m.invoke(courseDAO);
            if (list instanceof java.util.List<?> l) return l.size();
        } catch (Exception ignored) { }
        return 0;
    }

    private int countTodaysSessions() {
        try {
            java.sql.Connection conn = edu.university.sams.dao.DatabaseManager.getInstance().getConnection();
            // MySQL current date
            String sql = "SELECT COUNT(*) FROM lecture_sessions WHERE session_date = CURDATE()";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                 java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception ignored) { }
        return 0;
    }

    private String computeAttendanceRate() {
        // Try attendance_records table
        String[] queries = new String[]{
                "SELECT ROUND(AVG(CASE WHEN status='PRESENT' THEN 1 ELSE 0 END)*100,1) FROM attendance_records",
                "SELECT ROUND(AVG(CASE WHEN status='PRESENT' THEN 1 ELSE 0 END)*100,1) FROM attendance"
        };
        for (String sql : queries) {
            try {
                java.sql.Connection conn = edu.university.sams.dao.DatabaseManager.getInstance().getConnection();
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                     java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        double val = rs.getDouble(1);
                        if (!rs.wasNull()) return val + "%";
                    }
                }
            } catch (Exception ignored) { }
        }
        return "—";
    }

    private String queryDatabaseSizeMB() {
        try {
            java.sql.Connection conn = edu.university.sams.dao.DatabaseManager.getInstance().getConnection();
            String sql = "SELECT ROUND(SUM(data_length + index_length)/1024/1024,1) AS mb " +
                    "FROM information_schema.tables WHERE table_schema = DATABASE()";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                 java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double mb = rs.getDouble(1);
                    if (!rs.wasNull()) return mb + " MB";
                }
            }
        } catch (Exception ignored) { }
        return "—";
    }

    private String formatDuration(long millis) {
        long seconds = millis / 1000;
        long days = seconds / 86400; seconds %= 86400;
        long hours = seconds / 3600; seconds %= 3600;
        long minutes = seconds / 60;
        if (days > 0) return String.format("%d d %d h %d m", days, hours, minutes);
        if (hours > 0) return String.format("%d h %d m", hours, minutes);
        return String.format("%d m", minutes);
    }

    private void stylePrimary(JButton b) {
        ShadcnUI.Button.applyVariant(b, ShadcnUI.Variant.DEFAULT, ShadcnUI.Size.MD);
    }

    private void styleSecondary(JButton b) {
        ShadcnUI.Button.applyVariant(b, ShadcnUI.Variant.SECONDARY, ShadcnUI.Size.MD);
    }

    private void styleDanger(JButton b) {
        ShadcnUI.Button.applyVariant(b, ShadcnUI.Variant.DESTRUCTIVE, ShadcnUI.Size.MD);
    }

    private void refreshAll() {
        loadUsers();
        refreshStats();
        // Future: refresh courses, stats, recent activity, etc.
    }

    private String safe(String s) { return s == null ? "" : s.replaceAll("[\\r\\n,]", " "); }

    private String safeCount(CountSupplier supplier) {
        try { return String.valueOf(supplier.get()); } catch (Exception e) { return "0"; }
    }

    private String invokeStr(Object target, String method) {
        try { return String.valueOf(target.getClass().getMethod(method).invoke(target)); }
        catch (Exception e) { return ""; }
    }

    private String nonEmpty(String preferred, String fallback) {
        return (preferred != null && !preferred.isBlank()) ? preferred : (fallback == null ? "" : fallback);
    }

    private String safeStr(Object v) { return v == null ? "" : safe(String.valueOf(v)); }

    private void exportCoursesCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("courses_report.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter out = new FileWriter(chooser.getSelectedFile())) {
                out.write("ID,Code,Name,Credits,Semester,Instructor\n");
                try {
                    java.lang.reflect.Method m = courseDAO.getClass().getMethod("getAllCourses");
                    Object list = m.invoke(courseDAO);
                    if (list instanceof java.util.List<?> courses) {
                        for (Object c : courses) {
                            out.write(String.format("%s,%s,%s,%s,%s,%s%n",
                                    safe(invokeStr(c, "getCourseId")),
                                    safe(invokeStr(c, "getCourseCode")),
                                    safe(invokeStr(c, "getCourseName")),
                                    safe(invokeStr(c, "getCredits")),
                                    safe(invokeStr(c, "getSemester")),
                                    safe(nonEmpty(invokeStr(c, "getInstructorName"), invokeStr(c, "getInstructorId")))));
                        }
                    }
                } catch (Exception ignore) {
                    // If DAO method doesn't exist, export nothing
                }
                JOptionPane.showMessageDialog(this, "Exported successfully.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportSummaryTxt() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("summary.txt"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter out = new FileWriter(chooser.getSelectedFile())) {
                out.write("SAMS Summary Report\n");
                out.write("===================\n");
                out.write("Total Users: " + safeCount(() -> userDAO.getAllUsers().size()) + "\n");
                out.write("Students: " + safeCount(() -> userDAO.getAllStudents().size()) + "\n");
                out.write("Instructors: " + safeCount(() -> userDAO.getAllInstructors().size()) + "\n");
                out.write("Total Courses: " + countTotalCourses() + "\n");
                out.write("Today's Sessions: " + countTodaysSessions() + "\n");
                out.write("Attendance Rate: " + computeAttendanceRate() + "\n");
                out.write("Database Size: " + queryDatabaseSizeMB() + "\n");
                out.write("Uptime: " + formatDuration(System.currentTimeMillis() - startMillis) + "\n");
                JOptionPane.showMessageDialog(this, "Exported successfully.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @FunctionalInterface
    private interface CountSupplier { int get() throws Exception; }
}
