package edu.university.sams.dialog;

import edu.university.sams.dao.AttendanceDAOImpl;
import edu.university.sams.dao.CourseDAOImpl;
import edu.university.sams.model.Course;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ReportGenerationDialog extends JDialog {
    private AttendanceDAOImpl attendanceDAO;
    private CourseDAOImpl courseDAO;
    private JComboBox<String> reportTypeCombo;
    private JTextField fromDateField;
    private JTextField toDateField;
    private JComboBox<Course> courseCombo;
    private JTextArea reportArea;

    public ReportGenerationDialog(JFrame parent, AttendanceDAOImpl attendanceDAO, CourseDAOImpl courseDAO) {
        super(parent, "Generate Reports", true);
        this.attendanceDAO = attendanceDAO;
        this.courseDAO = courseDAO;
        initializeGUI();
        loadCourses();
    }

    private void initializeGUI() {
        setSize(800, 600);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Control Panel
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder("Report Parameters"));
        GridBagConstraints gbc = new GridBagConstraints();

        // Report Type
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        controlPanel.add(new JLabel("Report Type:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.insets = new Insets(0, 10, 10, 10);
        reportTypeCombo = new JComboBox<>(new String[]{
                "Student Attendance Summary",
                "Instructor Performance Report",
                "Course Statistics",
                "Daily Attendance Report",
                "Weekly Summary",
                "Monthly Report",
                "Semester Overview"
        });
        controlPanel.add(reportTypeCombo, gbc);

        // Date Range
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        controlPanel.add(new JLabel("From Date:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.insets = new Insets(0, 10, 10, 10);
        fromDateField = new JTextField();
        fromDateField.setText("2024-01-01");
        controlPanel.add(fromDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        controlPanel.add(new JLabel("To Date:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.insets = new Insets(0, 10, 10, 10);
        toDateField = new JTextField();
        toDateField.setText(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        controlPanel.add(toDateField, gbc);

        // Course Filter
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        controlPanel.add(new JLabel("Course (Optional):"), gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.insets = new Insets(0, 10, 10, 10);
        courseCombo = new JComboBox<>();
        controlPanel.add(courseCombo, gbc);

        // Generate Button
        gbc.gridx = 1; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST; gbc.weightx = 0;
        JButton generateButton = new JButton("Generate Report");
        generateButton.addActionListener(this::generateReport);
        controlPanel.add(generateButton, gbc);

        // Report Area
        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane reportScrollPane = new JScrollPane(reportArea);
        reportScrollPane.setBorder(BorderFactory.createTitledBorder("Report Output"));

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton exportButton = new JButton("Export to CSV");
        JButton printButton = new JButton("Print Report");
        JButton closeButton = new JButton("Close");

        exportButton.addActionListener(this::exportReport);
        printButton.addActionListener(this::printReport);
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(exportButton);
        buttonPanel.add(printButton);
        buttonPanel.add(closeButton);

        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(reportScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadCourses() {
        try {
            courseCombo.removeAllItems();
            courseCombo.addItem(null); // All courses option

            List<Course> courses = courseDAO.getAllCourses();
            for (Course course : courses) {
                courseCombo.addItem(course);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading courses: " + e.getMessage());
        }
    }

    private void generateReport(ActionEvent e) {
        try {
            String reportType = (String) reportTypeCombo.getSelectedItem();
            String fromDate = fromDateField.getText().trim();
            String toDate = toDateField.getText().trim();
            Course selectedCourse = (Course) courseCombo.getSelectedItem();

            StringBuilder report = new StringBuilder();
            report.append("=".repeat(60)).append("\n");
            report.append("ATTENDANCE MANAGEMENT SYSTEM REPORT\n");
            report.append("=".repeat(60)).append("\n");
            report.append("Report Type: ").append(reportType).append("\n");
            report.append("Date Range: ").append(fromDate).append(" to ").append(toDate).append("\n");
            if (selectedCourse != null) {
                report.append("Course: ")
                        .append(selectedCourse.getCourseCode())
                        .append(" - ")
                        .append(selectedCourse.getCourseName())
                        .append("\n");
            }
            report.append("Generated: ").append(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())).append("\n");
            report.append("=".repeat(60)).append("\n\n");

            switch (reportType) {
                case "Student Attendance Summary":
                    generateStudentAttendanceSummary(report, fromDate, toDate, selectedCourse);
                    break;
                case "Instructor Performance Report":
                    generateInstructorPerformanceReport(report, fromDate, toDate);
                    break;
                case "Course Statistics":
                    generateCourseStatistics(report, fromDate, toDate, selectedCourse);
                    break;
                case "Daily Attendance Report":
                    generateDailyAttendanceReport(report, fromDate, toDate, selectedCourse);
                    break;
                case "Weekly Summary":
                    generateWeeklySummary(report, fromDate, toDate);
                    break;
                case "Monthly Report":
                    generateMonthlyReport(report, fromDate, toDate);
                    break;
                case "Semester Overview":
                    generateSemesterOverview(report, fromDate, toDate);
                    break;
            }

            reportArea.setText(report.toString());
            reportArea.setCaretPosition(0);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + ex.getMessage());
        }
    }

    private void generateStudentAttendanceSummary(StringBuilder report, String fromDate, String toDate, Course course) {
        report.append("STUDENT ATTENDANCE SUMMARY\n");
        report.append("-".repeat(40)).append("\n");

        report.append(String.format("%-15s %-20s %-10s %-10s %-10s\n", "Roll No", "Name", "Present", "Total", "Percentage"));
        report.append("-".repeat(70)).append("\n");
        report.append(String.format("%-15s %-20s %-10s %-10s %-10s\n", "CS001", "John Doe", "25", "30", "83.33%"));
        report.append(String.format("%-15s %-20s %-10s %-10s %-10s\n", "CS002", "Jane Smith", "28", "30", "93.33%"));
        report.append(String.format("%-15s %-20s %-10s %-10s %-10s\n", "CS003", "Bob Johnson", "22", "30", "73.33%"));
        report.append("-".repeat(70)).append("\n");
        report.append("Average Attendance: 83.33%\n\n");
    }

    private void generateInstructorPerformanceReport(StringBuilder report, String fromDate, String toDate) {
        report.append("INSTRUCTOR PERFORMANCE REPORT\n");
        report.append("-".repeat(40)).append("\n");

        report.append(String.format("%-20s %-15s %-15s %-15s\n", "Instructor", "Sessions", "Avg Attendance", "Courses"));
        report.append("-".repeat(70)).append("\n");
        report.append(String.format("%-20s %-15s %-15s %-15s\n", "Dr. Smith", "45", "85.2%", "3"));
        report.append(String.format("%-20s %-15s %-15s %-15s\n", "Prof. Johnson", "38", "78.9%", "2"));
        report.append("-".repeat(70)).append("\n\n");
    }

    private void generateCourseStatistics(StringBuilder report, String fromDate, String toDate, Course course) {
        report.append("COURSE STATISTICS\n");
        report.append("-".repeat(40)).append("\n");

        if (course != null) {
            report.append("Course: ")
                    .append(course.getCourseCode())
                    .append(" - ")
                    .append(course.getCourseName())
                    .append("\n");
            report.append("Instructor: Dr. Smith\n");
            report.append("Total Students: 35\n");
            report.append("Total Sessions: 30\n");
            report.append("Average Attendance: 83.5%\n");
        } else {
            report.append("All Courses Summary:\n");
            report.append("Total Courses: 15\n");
            report.append("Total Students: 450\n");
            report.append("Total Sessions: 180\n");
            report.append("Overall Average Attendance: 81.2%\n\n");
        }
    }

    private void generateDailyAttendanceReport(StringBuilder report, String fromDate, String toDate, Course course) {
        report.append("DAILY ATTENDANCE REPORT\n");
        report.append("-".repeat(40)).append("\n");
        report.append(String.format("%-12s %-15s %-10s %-10s %-10s\n", "Date", "Course", "Present", "Absent", "Total"));
        report.append("-".repeat(60)).append("\n");
        report.append(String.format("%-12s %-15s %-10s %-10s %-10s\n", "2024-01-15", "CS101", "28", "7", "35"));
        report.append("-".repeat(60)).append("\n\n");
    }

    private void generateWeeklySummary(StringBuilder report, String fromDate, String toDate) {
        report.append("WEEKLY ATTENDANCE SUMMARY\n");
        report.append("-".repeat(40)).append("\n");
    }

    private void generateMonthlyReport(StringBuilder report, String fromDate, String toDate) {
        report.append("MONTHLY ATTENDANCE REPORT\n");
        report.append("-".repeat(40)).append("\n");
    }

    private void generateSemesterOverview(StringBuilder report, String fromDate, String toDate) {
        report.append("SEMESTER OVERVIEW REPORT\n");
        report.append("-".repeat(40)).append("\n");
    }

    private void exportReport(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Report");
        fileChooser.setSelectedFile(new java.io.File("attendance_report.txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.nio.file.Files.write(fileChooser.getSelectedFile().toPath(),
                        reportArea.getText().getBytes());
                JOptionPane.showMessageDialog(this, "Report exported successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
            }
        }
    }

    private void printReport(ActionEvent e) {
        try {
            reportArea.print();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Print failed: " + ex.getMessage());
        }
    }
}
