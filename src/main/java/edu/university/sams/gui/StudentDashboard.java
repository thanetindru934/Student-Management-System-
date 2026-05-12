package edu.university.sams.gui;

import edu.university.sams.model.Student;
import edu.university.sams.model.Course;
import edu.university.sams.service.CourseService;
import edu.university.sams.service.AttendanceService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.logging.Logger;

public class StudentDashboard extends JFrame {

    private static final Logger LOGGER = Logger.getLogger(StudentDashboard.class.getName());

    private Student student;
    private JTabbedPane tabbedPane;
    private CourseService courseService;
    private AttendanceService attendanceService;

    public StudentDashboard(Student student) {
        this.student = student;
        this.courseService = new CourseService();
        this.attendanceService = new AttendanceService();

        setTitle("SAMS - Student Dashboard - " + student.getFullName());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        setupLayout();
        loadData();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Dashboard", createDashboardPanel());
        tabbedPane.addTab("My Courses", createCoursesPanel());
        tabbedPane.addTab("Attendance Reports", createReportsPanel());
        tabbedPane.addTab("Profile", createProfilePanel());
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel welcomeLabel = new JLabel("Welcome, " + student.getFullName());
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
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel infoPanel = createStudentInfoPanel();
        JPanel summaryPanel = createAttendanceSummaryPanel();

        panel.add(infoPanel, BorderLayout.NORTH);
        panel.add(summaryPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStudentInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Student Information"));
        panel.setBackground(Color.WHITE);

        panel.add(createInfoLabel("Student ID:", student.getStudentId()));
        panel.add(createInfoLabel("Program:", student.getProgram()));
        panel.add(createInfoLabel("Semester:", String.valueOf(student.getSemester())));
        panel.add(createInfoLabel("Enrollment Year:", String.valueOf(student.getEnrollmentYear())));
        panel.add(createInfoLabel("Name:", student.getFullName()));
        panel.add(createInfoLabel("Email:", student.getEmail()));
        panel.add(createInfoLabel("Username:", student.getUsername()));
        panel.add(createInfoLabel("Status:", "Active"));

        return panel;
    }

    private JPanel createInfoLabel(String label, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Arial", Font.PLAIN, 12));

        panel.add(labelComp, BorderLayout.NORTH);
        panel.add(valueComp, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAttendanceSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Attendance Summary"));

        String[] columns = {"Course", "Total Sessions", "Present", "Absent", "Late", "Percentage"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(52, 152, 219));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columns = {"Course Code", "Course Name", "Instructor", "Credits", "Semester"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(46, 204, 113));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.add(new JLabel("Select Course:"));

        JComboBox<Course> courseCombo = new JComboBox<>();
        courseCombo.setPreferredSize(new Dimension(250, 25));
        selectionPanel.add(courseCombo);

        JButton viewReportButton = new JButton("View Report");
        viewReportButton.setBackground(new Color(155, 89, 182));
        viewReportButton.setForeground(Color.WHITE);
        viewReportButton.setFocusPainted(false);
        selectionPanel.add(viewReportButton);

        panel.add(selectionPanel, BorderLayout.NORTH);

        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Profile Panel - Under Construction"));
        return panel;
    }

    private void loadData() {
        // Stub: load student courses and attendance data
        LOGGER.info("Loading student data for " + student.getFullName());
    }

    private void logout() {
        dispose();
        SwingUtilities.invokeLater(() -> new LoginWindow().setVisible(true));
    }

    private void loadAttendanceReport(DefaultTableModel reportModel, Course course,
                                      JLabel totalLabel, JLabel presentLabel, JLabel absentLabel,
                                      JLabel lateLabel, JLabel percentageLabel) {
        // Stub: populate attendance report table
        reportModel.setRowCount(0);
        totalLabel.setText("Total: 0");
        presentLabel.setText("Present: 0");
        absentLabel.setText("Absent: 0");
        lateLabel.setText("Late: 0");
        percentageLabel.setText("Percentage: 0%");
    }
}
