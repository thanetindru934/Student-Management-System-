package edu.university.sams.service;

import edu.university.sams.dao.AttendanceDAO;
import edu.university.sams.dao.AttendanceDAOImpl;
import edu.university.sams.dao.CourseDAO;
import edu.university.sams.dao.CourseDAOImpl;
import edu.university.sams.dao.DatabaseManager;
import edu.university.sams.model.Student;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReportService {

    private static final Logger LOGGER = Logger.getLogger(ReportService.class.getName());

    private final AttendanceDAO attendanceDAO;
    private final CourseDAO courseDAO;
    private final DatabaseManager dbManager;

    public ReportService() {
        this.dbManager = DatabaseManager.getInstance();
        this.attendanceDAO = new AttendanceDAOImpl(dbManager);
        this.courseDAO = new CourseDAOImpl(dbManager);
    }

    public List<Map<String, Object>> generateCourseAttendanceReport(String courseId) {
        List<Map<String, Object>> report = new ArrayList<>();
        List<Student> students = courseDAO.getEnrolledStudents(courseId);

        for (Student student : students) {
            Map<String, Object> data = new HashMap<>();
            data.put("studentId", student.getStudentId());
            data.put("studentName", student.getFullName());
            data.putAll(attendanceDAO.getAttendanceStatistics(student.getStudentId(), courseId));
            report.add(data);
        }
        return report;
    }

    public Map<String, Object> generateOverallAttendanceReport() {
        Map<String, Object> report = new HashMap<>();
        try (Connection conn = dbManager.getConnection()) {
            String sql = "SELECT COUNT(DISTINCT student_id) AS total_students, " +
                    "COUNT(record_id) AS total_records, " +
                    "SUM(CASE WHEN attendance_status = 'PRESENT' THEN 1 ELSE 0 END) AS present_count " +
                    "FROM attendance_records";
            ResultSet rs = conn.prepareStatement(sql).executeQuery();
            if (rs.next()) {
                report.put("totalStudents", rs.getInt("total_students"));
                report.put("totalRecords", rs.getInt("total_records"));
                report.put("presentCount", rs.getInt("present_count"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error generating overall report", e);
        }
        return report;
    }
}
