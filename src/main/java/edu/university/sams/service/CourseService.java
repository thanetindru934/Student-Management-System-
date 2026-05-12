package edu.university.sams.service;

import edu.university.sams.dao.CourseDAO;
import edu.university.sams.dao.CourseDAOImpl;
import edu.university.sams.dao.DatabaseManager;
import edu.university.sams.model.Course;
import edu.university.sams.model.Student;

import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CourseService {

    private static final Logger LOGGER = Logger.getLogger(CourseService.class.getName());

    private final CourseDAO courseDAO;
    private final DatabaseManager dbManager;

    public CourseService() {
        this.dbManager = DatabaseManager.getInstance();
        this.courseDAO = new CourseDAOImpl(dbManager); // Pass DatabaseManager instance
    }

    public List<Course> getInstructorCourses(String instructorId) {
        return courseDAO.getCoursesByInstructor(instructorId);
    }

    public List<Course> getStudentCourses(String studentId) {
        return courseDAO.getCoursesByStudent(studentId);
    }

    public List<Student> getCourseStudents(String courseId) {
        return courseDAO.getEnrolledStudents(courseId);
    }

    public List<Course> getAllCourses() {
        return courseDAO.getAllCourses();
    }

    public Course getCourseById(String courseId) {
        return courseDAO.findById(courseId);
    }

    private String generateId(String prefix) {
        return prefix + System.currentTimeMillis();
    }

    public String createLectureSession(String courseId, String date, String time,
                                       int duration, String location, String topic) {
        try {
            String sessionId = generateId("SES");
            String sql = "INSERT INTO lecture_sessions (session_id, course_id, session_date, " +
                    "session_time, duration_minutes, location, topic, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, 'SCHEDULED')";

            PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql);
            pstmt.setString(1, sessionId);
            pstmt.setString(2, courseId);
            pstmt.setDate(3, Date.valueOf(date));
            pstmt.setTime(4, Time.valueOf(time + ":00"));
            pstmt.setInt(5, duration);
            pstmt.setString(6, location);
            pstmt.setString(7, topic);

            int result = pstmt.executeUpdate();
            if (result > 0) {
                LOGGER.info("Lecture session created: " + sessionId);
                return sessionId;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating lecture session: " + e.getMessage(), e);
        }
        return null;
    }

    public List<String> getCourseSessions(String courseId) {
        List<String> sessions = new java.util.ArrayList<>();
        try {
            String sql = "SELECT session_id, session_date, session_time, topic, status " +
                    "FROM lecture_sessions WHERE course_id = ? " +
                    "ORDER BY session_date DESC, session_time DESC";

            PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql);
            pstmt.setString(1, courseId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String sessionInfo = rs.getString("session_id") + " - " +
                        rs.getDate("session_date") + " " +
                        rs.getTime("session_time") +
                        (rs.getString("topic") != null ? " (" + rs.getString("topic") + ")" : "") +
                        " [" + rs.getString("status") + "]";
                sessions.add(sessionInfo);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving course sessions: " + e.getMessage(), e);
        }
        return sessions;
    }

    public boolean saveCourse(Course course) {
        return courseDAO.save(course);
    }

    public boolean updateCourse(Course course) {
        return courseDAO.update(course);
    }

    public boolean deleteCourse(String courseId) {
        return courseDAO.delete(courseId);
    }

    public boolean isStudentEnrolled(String studentId, String courseId) {
        return courseDAO.isStudentEnrolled(studentId, courseId);
    }
}
