package edu.university.sams.dao;

import edu.university.sams.model.AttendanceRecord;
import edu.university.sams.model.enums.AttendanceStatus;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AttendanceDAOImpl implements AttendanceDAO {

    private static final Logger LOGGER = Logger.getLogger(AttendanceDAOImpl.class.getName());
    private final DatabaseManager dbManager;

    public AttendanceDAOImpl(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public boolean save(AttendanceRecord record) {
        String sql = "INSERT INTO attendance_records (record_id, student_id, session_id, " +
                "course_id, instructor_id, attendance_status, remarks) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, record.getRecordId());
            stmt.setString(2, record.getStudentId());
            stmt.setString(3, record.getSessionId());
            stmt.setString(4, record.getCourseId());
            stmt.setString(5, record.getInstructorId());
            stmt.setString(6, record.getStatus().name());
            stmt.setString(7, record.getRemarks());

            int result = stmt.executeUpdate();
            LOGGER.info("Attendance record saved: " + record.getRecordId());
            return result > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving attendance record: " + e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean update(AttendanceRecord record) {
        String sql = "UPDATE attendance_records SET attendance_status = ?, remarks = ?, " +
                "modified_at = CURRENT_TIMESTAMP WHERE record_id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, record.getStatus().name());
            stmt.setString(2, record.getRemarks());
            stmt.setString(3, record.getRecordId());

            int result = stmt.executeUpdate();
            LOGGER.info("Attendance record updated: " + record.getRecordId());
            return result > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating attendance record: " + e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean delete(String recordId) {
        String sql = "DELETE FROM attendance_records WHERE record_id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, recordId);
            int result = stmt.executeUpdate();
            LOGGER.info("Attendance record deleted: " + recordId);
            return result > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting attendance record: " + e.getMessage(), e);
            return false;
        }
    }

    @Override
    public AttendanceRecord findById(String recordId) {
        String sql = "SELECT * FROM attendance_records WHERE record_id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, recordId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToRecord(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding attendance record: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<AttendanceRecord> getStudentAttendance(String studentId, String courseId) {
        List<AttendanceRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM attendance_records WHERE student_id = ? AND course_id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setString(2, courseId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                records.add(mapResultSetToRecord(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching student attendance: " + e.getMessage(), e);
        }
        return records;
    }

    @Override
    public boolean existsAttendance(String studentId, String sessionId) {
        String sql = "SELECT 1 FROM attendance_records WHERE student_id = ? AND session_id = ? LIMIT 1";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setString(2, sessionId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking attendance existence: " + e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getAttendanceStatistics(String studentId, String courseId) {
        Map<String, Object> stats = new HashMap<>();
        for (AttendanceStatus status : AttendanceStatus.values()) {
            stats.put(status.name(), 0);
        }

        String sql = "SELECT attendance_status, COUNT(*) AS count " +
                "FROM attendance_records WHERE student_id = ? AND course_id = ? " +
                "GROUP BY attendance_status";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setString(2, courseId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stats.put(rs.getString("attendance_status"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching attendance statistics: " + e.getMessage(), e);
        }
        return stats;
    }

    @Override
    public List<AttendanceRecord> getCourseAttendance(String courseId) {
        List<AttendanceRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM attendance_records WHERE course_id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, courseId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                records.add(mapResultSetToRecord(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching course attendance: " + e.getMessage(), e);
        }
        return records;
    }

    private AttendanceRecord mapResultSetToRecord(ResultSet rs) throws SQLException {
        AttendanceRecord record = new AttendanceRecord();
        record.setRecordId(rs.getString("record_id"));
        record.setStudentId(rs.getString("student_id"));
        record.setSessionId(rs.getString("session_id"));
        record.setCourseId(rs.getString("course_id"));
        record.setInstructorId(rs.getString("instructor_id"));
        record.setStatus(AttendanceStatus.valueOf(rs.getString("attendance_status")));
        Timestamp timestamp = rs.getTimestamp("marked_at");
        if (timestamp != null) {
            record.setMarkedAt(timestamp.toLocalDateTime());
        }
        record.setRemarks(rs.getString("remarks"));
        return record;
    }
}
