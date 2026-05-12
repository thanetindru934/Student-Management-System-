package edu.university.sams.dao;

import edu.university.sams.model.User;
import edu.university.sams.model.Student;
import edu.university.sams.model.Instructor;
import edu.university.sams.model.enums.UserRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAOImpl implements UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAOImpl.class.getName());
    private final DatabaseManager dbManager;

    public UserDAOImpl(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public UserDAOImpl() {
        this.dbManager = DatabaseManager.getInstance();
    }

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT u.*, s.student_id, s.program, s.semester, s.enrollment_year, " +
                "i.instructor_id, i.department, i.employee_id, " +
                "a.admin_id, a.department as admin_dept, a.access_level " +
                "FROM users u " +
                "LEFT JOIN students s ON u.user_id = s.user_id " +
                "LEFT JOIN instructors i ON u.user_id = i.user_id " +
                "LEFT JOIN administrators a ON u.user_id = a.user_id " +
                "WHERE u.username = ? AND u.is_active = TRUE";

        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by username: " + e.getMessage(), e);
        }
        return null;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        UserRole role = UserRole.valueOf(rs.getString("role"));

        switch (role) {
            case STUDENT:
                Student student = new Student();
                student.setUserId(rs.getString("user_id"));
                student.setUsername(rs.getString("username"));
                student.setEmail(rs.getString("email"));
                student.setPasswordHash(rs.getString("password_hash"));
                student.setRole(role);
                student.setFirstName(rs.getString("first_name"));
                student.setLastName(rs.getString("last_name"));
                student.setActive(rs.getBoolean("is_active"));
                student.setPhone(rs.getString("phone"));
                student.setStudentId(rs.getString("student_id"));
                student.setProgram(rs.getString("program"));
                student.setSemester(rs.getInt("semester"));
                student.setEnrollmentYear(rs.getInt("enrollment_year"));
                return student;

            case INSTRUCTOR:
                Instructor instructor = new Instructor();
                instructor.setUserId(rs.getString("user_id"));
                instructor.setUsername(rs.getString("username"));
                instructor.setEmail(rs.getString("email"));
                instructor.setPasswordHash(rs.getString("password_hash"));
                instructor.setRole(role);
                instructor.setFirstName(rs.getString("first_name"));
                instructor.setLastName(rs.getString("last_name"));
                instructor.setActive(rs.getBoolean("is_active"));
                instructor.setPhone(rs.getString("phone"));
                instructor.setInstructorId(rs.getString("instructor_id"));
                instructor.setDepartment(rs.getString("department"));
                instructor.setEmployeeId(rs.getString("employee_id"));
                return instructor;

            case ADMINISTRATOR:
            default:
                User admin = new User();
                admin.setUserId(rs.getString("user_id"));
                admin.setUsername(rs.getString("username"));
                admin.setEmail(rs.getString("email"));
                admin.setPasswordHash(rs.getString("password_hash"));
                admin.setRole(role);
                admin.setFirstName(rs.getString("first_name"));
                admin.setLastName(rs.getString("last_name"));
                admin.setActive(rs.getBoolean("is_active"));
                admin.setPhone(rs.getString("phone"));
                return admin;
        }
    }

    @Override
    public User findById(String userId) {
        // Implementation similar to findByUsername but with user_id
        return null;
    }

    @Override
    public boolean save(User user) {
        return false;
    }

    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>();
    }

    @Override
    public List<Student> getAllStudents() {
        return new ArrayList<>();
    }

    @Override
    public List<Instructor> getAllInstructors() {
        return new ArrayList<>();
    }

    @Override
    public boolean setUserActive(String userId, boolean active) {
        return false;
    }

    @Override
    public boolean resetPassword(String userId, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, newPasswordHash);
            stmt.setString(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error resetting password: " + e.getMessage(), e);
            return false;
        }
    }
}