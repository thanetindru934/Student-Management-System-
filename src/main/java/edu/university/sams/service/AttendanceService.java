package edu.university.sams.service;

import edu.university.sams.dao.AttendanceDAO;
import edu.university.sams.dao.AttendanceDAOImpl;
import edu.university.sams.dao.CourseDAO;
import edu.university.sams.dao.CourseDAOImpl;
import edu.university.sams.dao.DatabaseManager;
import edu.university.sams.model.AttendanceRecord;
import edu.university.sams.model.enums.AttendanceStatus;

import java.util.List;
import java.util.logging.Logger;

import edu.university.sams.dao.*;
import edu.university.sams.model.AttendanceRecord;
import edu.university.sams.model.enums.AttendanceStatus;
import edu.university.sams.service.exceptions.AttendanceAlreadyMarkedException;
import edu.university.sams.service.exceptions.AttendanceWindowClosedException;
import edu.university.sams.service.exceptions.UnauthorizedAccessException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

public class AttendanceService {

    private static final Logger LOGGER = Logger.getLogger(AttendanceService.class.getName());

    private final AttendanceDAO attendanceDAO;
    private final CourseDAO courseDAO;
    private final ValidationService validationService;

    public AttendanceService() {
        DatabaseManager dbManager = DatabaseManager.getInstance();
        this.attendanceDAO = new AttendanceDAOImpl(dbManager);
        this.courseDAO = new CourseDAOImpl(dbManager);
        this.validationService = new ValidationService();
    }

    // Overloaded constructor for tests (dependency injection)
    public AttendanceService(AttendanceDAO attendanceDAO, CourseDAO courseDAO, ValidationService validationService) {
        this.attendanceDAO = attendanceDAO;
        this.courseDAO = courseDAO;
        this.validationService = (validationService != null) ? validationService : new ValidationService();
    }

    private String generateId(String prefix) {
        return prefix + System.currentTimeMillis();
    }

    public boolean markAttendance(String studentId, String courseId, String instructorId,
                                  AttendanceStatus status, String sessionId, String remarks) {
        if (!validationService.validateAttendanceInput(studentId, courseId, instructorId, status)) {
            LOGGER.warning("Invalid attendance input");
            return false;
        }

        if (!courseDAO.isStudentEnrolled(studentId, courseId)) {
            LOGGER.warning("Student not enrolled: " + studentId + " -> " + courseId);
            return false;
        }

        // Ensure instructor is authorized for this course (UT-ATT-004)
        if (!isInstructorAuthorized(instructorId, courseId)) {
            throw new UnauthorizedAccessException("Instructor " + instructorId + " is not authorized for course " + courseId);
        }

        // Enforce a 24-hour window after session date in sessionId (format e.g., CS101_yyyyMMdd) (UT-ATT-003)
        if (isWindowClosed(sessionId)) {
            throw new AttendanceWindowClosedException("Attendance window is closed for session: " + sessionId);
        }

        // Prevent duplicates for same student/session (UT-ATT-002)
        AttendanceRecord existing = getAttendanceBySessionAndStudent(studentId, sessionId, courseId);
        if (existing != null) {
            throw new AttendanceAlreadyMarkedException("Attendance already exists for " + studentId + " : " + sessionId);
        }

        AttendanceRecord record = new AttendanceRecord();
        record.setRecordId(generateId("REC"));
        record.setStudentId(studentId);
        record.setCourseId(courseId);
        record.setInstructorId(instructorId);
        record.setSessionId(sessionId);
        record.setStatus(status);
        record.setRemarks(remarks);
        record.setMarkedAt(java.time.LocalDateTime.now());

        boolean result = attendanceDAO.save(record);
        if (result) {
            LOGGER.info("Attendance marked: " + studentId + " -> " + status);
        }
        return result;
    }

    public List<AttendanceRecord> getStudentAttendance(String studentId, String courseId) {
        return attendanceDAO.getStudentAttendance(studentId, courseId);
    }

    public List<AttendanceRecord> getCourseAttendance(String courseId) {
        return attendanceDAO.getCourseAttendance(courseId);
    }

    // Fixed: previously searched with empty course id
    private AttendanceRecord getAttendanceBySessionAndStudent(String studentId, String sessionId, String courseId) {
        List<AttendanceRecord> records = attendanceDAO.getCourseAttendance(courseId);
        for (AttendanceRecord record : records) {
            if (record.getStudentId().equals(studentId) && record.getSessionId().equals(sessionId)) {
                return record;
            }
        }
        return null;
    }

    // Authorization: try CourseDAO via reflection first, then optional DB fallback; else allow false-negative safe
    private boolean isInstructorAuthorized(String instructorId, String courseId) {
        try {
            // Attempt to call a conventional DAO method if present
            var m = courseDAO.getClass().getMethod("isInstructorAssigned", String.class, String.class);
            Object r = m.invoke(courseDAO, instructorId, courseId);
            if (r instanceof Boolean b) return b;
        } catch (Exception ignored) {}

        try {
            var m = courseDAO.getClass().getMethod("isInstructorAssignedToCourse", String.class, String.class);
            Object r = m.invoke(courseDAO, instructorId, courseId);
            if (r instanceof Boolean b) return b;
        } catch (Exception ignored) {}

        // Last resort: DB fallback if a mapping table exists (best-effort)
        try {
            var conn = DatabaseManager.getInstance().getConnection();
            try (var ps = conn.prepareStatement("SELECT 1 FROM course_instructors WHERE instructor_id = ? AND course_id = ? LIMIT 1")) {
                ps.setString(1, instructorId);
                ps.setString(2, courseId);
                try (var rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (Exception ignored) {}

        return false;
    }

    // Parse yyyyMMdd from sessionId ending (e.g., CS101_20241215) and allow marking until end of next day
    private boolean isWindowClosed(String sessionId) {
        try {
            int idx = sessionId.lastIndexOf('_');
            if (idx < 0) return false;
            String yyyymmdd = sessionId.substring(idx + 1);
            LocalDate date = LocalDate.parse(yyyymmdd, DateTimeFormatter.BASIC_ISO_DATE);
            LocalDate closes = date.plusDays(1); // end of next day
            return LocalDate.now().isAfter(closes);
        } catch (Exception e) {
            return false; // fail-open if parsing not possible
        }
    }
}
