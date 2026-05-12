package src.test.java.edu.university.sams.service;

import edu.university.sams.dao.AttendanceDAO;
import edu.university.sams.dao.CourseDAO;
import edu.university.sams.model.AttendanceRecord;
import edu.university.sams.model.enums.AttendanceStatus;
import edu.university.sams.service.AttendanceService;
import edu.university.sams.service.ValidationService;
import edu.university.sams.service.exceptions.AttendanceAlreadyMarkedException;
import edu.university.sams.service.exceptions.AttendanceWindowClosedException;
import edu.university.sams.service.exceptions.UnauthorizedAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class AttendanceServiceTest {

    private InMemoryAttendanceDAO attendanceDAO;
    private InMemoryCourseDAO courseDAO;
    private ValidationService validation;
    private AttendanceService svc;

    @BeforeEach
    void setup() {
        attendanceDAO = new InMemoryAttendanceDAO();
        courseDAO = new InMemoryCourseDAO();
        validation = new ValidationService() {
            @Override
            public boolean validateAttendanceInput(String studentId, String courseId, String instructorId, AttendanceStatus status) {
                return studentId != null && courseId != null && instructorId != null && status != null;
            }
        };
        // enroll and authorize defaults
        courseDAO.enroll("STU001", "CS101");
        courseDAO.authorize("INST001", "CS101");

        // AttendanceService with injected fakes; requires overloaded constructor
        svc = new AttendanceService(attendanceDAO, courseDAO, validation);
    }

    @Test
    void UT_ATT_001_validAttendanceRecording() {
        boolean ok = svc.markAttendance("STU001", "CS101", "INST001",
                AttendanceStatus.PRESENT, "CS101_20991231", "On time");
        assertTrue(ok);
        List<AttendanceRecord> list = attendanceDAO.getStudentAttendance("STU001", "CS101");
        assertEquals(1, list.size());
        AttendanceRecord r = list.get(0);
        assertEquals("STU001", r.getStudentId());
        assertEquals("CS101_20991231", r.getSessionId());
        assertEquals(AttendanceStatus.PRESENT, r.getStatus());
        assertNotNull(r.getMarkedAt());
        assertEquals("INST001", r.getInstructorId());
    }

    @Test
    void UT_ATT_002_duplicateAttendancePrevention() {
        svc.markAttendance("STU001", "CS101", "INST001",
                AttendanceStatus.PRESENT, "CS101_20991231", "On time");
        assertThrows(AttendanceAlreadyMarkedException.class, () ->
                svc.markAttendance("STU001", "CS101", "INST001",
                        AttendanceStatus.LATE, "CS101_20991231", "Late"));
    }

    @Test
    void UT_ATT_003_attendanceWindowValidation() {
        // Session far in the past -> window closed
        assertThrows(AttendanceWindowClosedException.class, () ->
                svc.markAttendance("STU001", "CS101", "INST001",
                        AttendanceStatus.PRESENT, "CS101_20000101", "too late"));
    }

    @Test
    void UT_ATT_004_unauthorizedInstructorAccess() {
        // INST002 is not authorized for CS101
        assertThrows(UnauthorizedAccessException.class, () ->
                svc.markAttendance("STU001", "CS101", "INST002",
                        AttendanceStatus.PRESENT, "CS101_20991231", "no auth"));
    }

    // --- In-memory fakes ---

    static class InMemoryAttendanceDAO implements AttendanceDAO {
        private final Map<String, List<AttendanceRecord>> byCourse = new HashMap<>();

        @Override
        public boolean save(AttendanceRecord record) {
            byCourse.computeIfAbsent(record.getCourseId(), k -> new ArrayList<>()).add(record);
            return true;
        }

        @Override
        public boolean update(AttendanceRecord record) {
            // not used in these tests (we throw on duplicate)
            return true;
        }

        @Override
        public java.util.List<AttendanceRecord> getStudentAttendance(String studentId, String courseId) {
            List<AttendanceRecord> list = new ArrayList<>();
            for (AttendanceRecord r : getCourseAttendance(courseId)) {
                if (r.getStudentId().equals(studentId)) list.add(r);
            }
            return list;
        }

        @Override
        public java.util.List<AttendanceRecord> getCourseAttendance(String courseId) {
            return new ArrayList<>(byCourse.getOrDefault(courseId, List.of()));
        }
    }

    static class InMemoryCourseDAO implements CourseDAO {
        private final Set<String> enrollments = new HashSet<>();
        private final Set<String> auth = new HashSet<>();
        void enroll(String studentId, String courseId) {
            enrollments.add(studentId + "::" + courseId);
        }
        void authorize(String instructorId, String courseId) {
            auth.add(instructorId + "::" + courseId);
        }
        @Override
        public boolean isStudentEnrolled(String studentId, String courseId) {
            return enrollments.contains(studentId + "::" + courseId);
        }
        // Reflection in AttendanceService will find this:
        public boolean isInstructorAssigned(String instructorId, String courseId) {
            return auth.contains(instructorId + "::" + courseId);
        }
    }
}
