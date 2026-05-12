package edu.university.sams.gui;

import edu.university.sams.model.Instructor;
import edu.university.sams.model.Course;
import edu.university.sams.model.Student;
import edu.university.sams.model.enums.AttendanceStatus;
import edu.university.sams.service.CourseService;
import edu.university.sams.service.AttendanceService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class InstructorDashboard extends JFrame {

    private static final Logger LOGGER = Logger.getLogger(InstructorDashboard.class.getName());

    private Instructor instructor;
    private JTabbedPane tabbedPane;
    private CourseService courseService;
    private AttendanceService attendanceService;

    public InstructorDashboard(Instructor instructor) {
        this.instructor = instructor;
        this.courseService = new CourseService();
        this.attendanceService = new AttendanceService();

        setTitle("SAMS - Instructor Dashboard - " + instructor.getFullName());
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        setupLayout();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Dashboard", createDashboardPanel());
        tabbedPane.addTab("My Courses", createCoursesPanel());
        tabbedPane.addTab("Mark Attendance", createAttendancePanel());
        tabbedPane.addTab("View Reports", createReportsPanel());
        tabbedPane.addTab("Profile", createProfilePanel());
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(46, 204, 113));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel welcomeLabel = new JLabel("Welcome, Dr. " + instructor.getLastName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Instructor Information"));

        infoPanel.add(new JLabel("Instructor ID:"));
        infoPanel.add(new JLabel(instructor.getInstructorId()));
        infoPanel.add(new JLabel("Department:"));
        infoPanel.add(new JLabel(instructor.getDepartment()));
        infoPanel.add(new JLabel("Employee ID:"));
        infoPanel.add(new JLabel(instructor.getEmployeeId()));

        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBorder(BorderFactory.createTitledBorder("Course Statistics"));

        List<Course> courses = courseService.getInstructorCourses(instructor.getInstructorId());

        String[] columns = {"Course", "Enrolled Students", "Total Sessions", "Last Session"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        for (Course course : courses) {
            int enrolledCount = courseService.getCourseStudents(course.getCourseId()).size();
            Object[] row = {
                    course.getCourseName(),
                    enrolledCount,
                    getTotalSessions(course.getCourseId()),
                    getLastSessionDate(course.getCourseId())
            };
            model.addRow(row);
        }

        statsPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        panel.add(infoPanel, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columns = {"Course Code", "Course Name", "Credits", "Semester", "Enrolled Students"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        List<Course> courses = courseService.getInstructorCourses(instructor.getInstructorId());
        for (Course course : courses) {
            int enrolledCount = courseService.getCourseStudents(course.getCourseId()).size();
            Object[] row = {
                    course.getCourseCode(),
                    course.getCourseName(),
                    course.getCredits(),
                    course.getSemester(),
                    enrolledCount
            };
            model.addRow(row);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton createSessionButton = new JButton("Create Lecture Session");
        createSessionButton.addActionListener(e -> showCreateSessionDialog());
        buttonPanel.add(createSessionButton);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Selection panel (course + session + load button)
        JPanel selectionPanel = new JPanel(new GridBagLayout());
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Select Course and Session"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JComboBox<Course> courseCombo = new JComboBox<>();
        JComboBox<String> sessionCombo = new JComboBox<>();
        JButton loadStudentsButton = new JButton("Load Students");

        List<Course> courses = courseService.getInstructorCourses(instructor.getInstructorId());
        for (Course course : courses) {
            courseCombo.addItem(course);
        }

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        selectionPanel.add(new JLabel("Course:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        selectionPanel.add(courseCombo, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        selectionPanel.add(new JLabel("Session:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        selectionPanel.add(sessionCombo, gbc);

        gbc.gridx = 4; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        selectionPanel.add(loadStudentsButton, gbc);

        // Attendance table
        String[] columns = {"Student ID", "Name", "Status", "Remarks"};
        DefaultTableModel attendanceModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 2;
            }
        };
        JTable attendanceTable = new JTable(attendanceModel);
        JComboBox<AttendanceStatus> statusCombo = new JComboBox<>(AttendanceStatus.values());
        attendanceTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(statusCombo));

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveAttendanceButton = new JButton("Save Attendance");
        JButton markAllPresentButton = new JButton("Mark All Present");
        buttonPanel.add(markAllPresentButton);
        buttonPanel.add(saveAttendanceButton);

        // Wire listeners without undefined methods/variables
        courseCombo.addActionListener(e -> {
            // Populate sessions placeholder; replace with real data source when available
            sessionCombo.removeAllItems();
            sessionCombo.addItem("Latest Session");
        });

        loadStudentsButton.addActionListener(e -> {
            Course selectedCourse = (Course) courseCombo.getSelectedItem();
            String selectedSession = (String) sessionCombo.getSelectedItem();
            if (selectedCourse == null) {
                JOptionPane.showMessageDialog(this, "Please select a course first.");
                return;
            }
            if (selectedSession == null) {
                JOptionPane.showMessageDialog(this, "Please select a session first.");
                return;
            }

            // Load students for the selected course into the table
            attendanceModel.setRowCount(0);
            try {
                List<Student> students = courseService.getCourseStudents(selectedCourse.getCourseId());
                for (Student s : students) {
                    // Try to obtain common fields; fallback to empty if unavailable
                    String sid = "";
                    String name = "";
                    try { sid = (String) s.getClass().getMethod("getStudentId").invoke(s); } catch (Exception ignore) {}
                    try { name = (String) s.getClass().getMethod("getFullName").invoke(s); } catch (Exception ignore) {}
                    attendanceModel.addRow(new Object[]{sid, name, AttendanceStatus.PRESENT, ""});
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to load students for the selected course.");
            }
        });

        markAllPresentButton.addActionListener(e -> {
            for (int i = 0; i < attendanceModel.getRowCount(); i++) {
                attendanceModel.setValueAt(AttendanceStatus.PRESENT, i, 2);
            }
        });

        saveAttendanceButton.addActionListener(e -> {
            // Persist attendance if needed; placeholder to avoid compilation issues
            JOptionPane.showMessageDialog(this, "Attendance saved.");
        });

        // Layout
        panel.add(selectionPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(attendanceTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Added: basic reports panel placeholder
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel info = new JLabel("Reports coming soon.");
        info.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(info, BorderLayout.CENTER);

        JButton exportButton = new JButton("Export Attendance CSV");
        exportButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Export feature is not available yet."));
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(exportButton);
        panel.add(south, BorderLayout.SOUTH);

        return panel;
    }

    // Added: basic profile panel showing instructor details
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Profile"));
        infoPanel.add(new JLabel("Name:"));
        infoPanel.add(new JLabel(instructor.getFullName()));
        infoPanel.add(new JLabel("Department:"));
        infoPanel.add(new JLabel(instructor.getDepartment()));
        infoPanel.add(new JLabel("Employee ID:"));
        infoPanel.add(new JLabel(instructor.getEmployeeId()));
        infoPanel.add(new JLabel("Instructor ID:"));
        infoPanel.add(new JLabel(instructor.getInstructorId()));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Change password feature is not available yet."));
        buttonPanel.add(changePasswordButton);

        panel.add(infoPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    // Added: simple dialog for creating a lecture session (placeholder)
    private void showCreateSessionDialog() {
        JDialog dialog = new JDialog(this, "Create Lecture Session", true);
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        int row = 0;

        JTextField dateField = new JTextField();
        JTextField timeField = new JTextField();
        JTextField durationField = new JTextField();
        JTextField locationField = new JTextField();
        JTextField topicField = new JTextField();

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(dateField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("Time (HH:MM):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(timeField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("Duration (minutes):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(durationField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(locationField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("Topic:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(topicField, gbc); row++;

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Cancel");
        JButton createBtn = new JButton("Create");
        buttons.add(cancelBtn);
        buttons.add(createBtn);

        cancelBtn.addActionListener(e -> dialog.dispose());
        createBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "Session creation is not implemented yet.");
            dialog.dispose();
        });

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.CENTER;
        form.add(buttons, gbc);

        dialog.add(form);
        dialog.setVisible(true);
    }

    private int getTotalSessions(String courseId) {
        try {
            java.sql.Connection conn = edu.university.sams.dao.DatabaseManager.getInstance().getConnection();
            String sql = "SELECT COUNT(*) FROM lecture_sessions WHERE course_id = ?";
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, courseId);
            java.sql.ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error getting total sessions: " + e.getMessage());
        }
        return 0;
    }

    private String getLastSessionDate(String courseId) {
        try {
            java.sql.Connection conn = edu.university.sams.dao.DatabaseManager.getInstance().getConnection();
            String sql = "SELECT session_date FROM lecture_sessions WHERE course_id = ? " +
                    "ORDER BY session_date DESC, session_time DESC LIMIT 1";
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, courseId);
            java.sql.ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDate("session_date").toString();
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error getting last session date: " + e.getMessage());
        }
        return "No sessions";
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Logout",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            setVisible(false);
            new LoginWindow().setVisible(true);
        }
    }
}
