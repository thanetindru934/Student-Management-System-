package edu.university.sams.dao;

import edu.university.sams.model.Course;
import edu.university.sams.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CourseDAOImpl implements CourseDAO {

    private static final Logger LOGGER = Logger.getLogger(CourseDAOImpl.class.getName());
    private final DatabaseManager dbManager;

    public CourseDAOImpl(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public Course findById(String courseId) {
        String sql = "SELECT * FROM courses WHERE course_id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, courseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToCourse(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding course by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY course_code";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all courses", e);
        }
        return courses;
    }

    @Override
    public List<Course> getCoursesByInstructor(String instructorId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE instructor_id = ? ORDER BY course_code";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, instructorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving instructor courses", e);
        }
        return courses;
    }

    @Override
    public List<Course> getCoursesByStudent(String studentId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.* FROM courses c " +
                "JOIN enrollments e ON c.course_id = e.course_id " +
                "WHERE e.student_id = ? AND e.status = 'ACTIVE' " +
                "ORDER BY c.course_code";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving student courses", e);
        }
        return courses;
    }

    @Override
    public List<Student> getEnrolledStudents(String courseId) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT u.*, s.* FROM users u " +
                "JOIN students s ON u.user_id = s.user_id " +
                "JOIN enrollments e ON s.student_id = e.student_id " +
                "WHERE e.course_id = ? AND e.status = 'ACTIVE' " +
                "ORDER BY u.last_name, u.first_name";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, courseId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Student student = new Student();
                student.setUserId(rs.getString("user_id"));
                student.setUsername(rs.getString("username"));
                student.setEmail(rs.getString("email"));
                student.setFirstName(rs.getString("first_name"));
                student.setLastName(rs.getString("last_name"));
                student.setStudentId(rs.getString("student_id"));
                student.setProgram(rs.getString("program"));
                student.setSemester(rs.getInt("semester"));
                student.setEnrollmentYear(rs.getInt("enrollment_year"));
                students.add(student);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving enrolled students", e);
        }
        return students;
    }

    @Override
    public boolean isStudentEnrolled(String studentId, String courseId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND course_id = ? AND status = 'ACTIVE'";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setString(2, courseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking student enrollment", e);
        }
        return false;
    }

    @Override
    public boolean save(Course course) {
        String sql = "INSERT INTO courses (course_id, course_code, course_name, instructor_id, " +
                "credits, semester, academic_year, max_capacity, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, course.getCourseId());
            stmt.setString(2, course.getCourseCode());
            stmt.setString(3, course.getCourseName());
            stmt.setString(4, course.getInstructorId());
            stmt.setInt(5, course.getCredits());
            stmt.setString(6, course.getSemester());
            stmt.setString(7, course.getAcademicYear());
            stmt.setInt(8, course.getMaxCapacity());
            stmt.setString(9, course.getDescription());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving course", e);
        }
        return false;
    }

    @Override
    public boolean update(Course course) {
        String sql = "UPDATE courses SET course_code = ?, course_name = ?, instructor_id = ?, " +
                "credits = ?, semester = ?, academic_year = ?, max_capacity = ?, description = ? " +
                "WHERE course_id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getCourseName());
            stmt.setString(3, course.getInstructorId());
            stmt.setInt(4, course.getCredits());
            stmt.setString(5, course.getSemester());
            stmt.setString(6, course.getAcademicYear());
            stmt.setInt(7, course.getMaxCapacity());
            stmt.setString(8, course.getDescription());
            stmt.setString(9, course.getCourseId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating course", e);
        }
        return false;
    }

    @Override
    public boolean delete(String courseId) {
        String sql = "DELETE FROM courses WHERE course_id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, courseId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting course", e);
        }
        return false;
    }

    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setCourseId(rs.getString("course_id"));
        course.setCourseCode(rs.getString("course_code"));
        course.setCourseName(rs.getString("course_name"));
        course.setInstructorId(rs.getString("instructor_id"));
        course.setCredits(rs.getInt("credits"));
        course.setSemester(rs.getString("semester"));
        course.setAcademicYear(rs.getString("academic_year"));
        course.setMaxCapacity(rs.getInt("max_capacity"));
        course.setDescription(rs.getString("description"));
        return course;
    }
}
