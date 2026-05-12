package edu.university.sams.integration;

import edu.university.sams.dao.AttendanceDAO;
import edu.university.sams.dao.CourseDAO;
import edu.university.sams.model.AttendanceRecord;
import edu.university.sams.model.enums.AttendanceStatus;
import edu.university.sams.service.AttendanceService;
import edu.university.sams.service.ValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AttendanceFlowIntegrationTest {

    private AttendanceService svc;
    private InMemAttendance attendance;
    private InMemCourse courses;

    @BeforeEach
    void setup() {
        attendance = new InMemAttendance();
        courses = new InMemCourse();
        courses.enroll("STU001", "CS101");
        courses.enroll("STU002", "CS101");
        courses.authorize("INST001", "CS101");

        ValidationService validation = new ValidationService();
        svc = new AttendanceService(attendance, courses, validation);
    }

    @Test
    void IT_FLOW_001_completeAttendanceWorkflow() {
        // 1. Mark multiple students
        assertTrue(svc.markAttendance("STU001", "CS101", "INST001",
                AttendanceStatus.PRESENT, "CS101_20991231", "ok"));
        assertTrue(svc.markAttendance("STU002", "CS101", "INST001",
                AttendanceStatus.LATE, "CS101_20991231", "late"));

        // 2. Verify persisted
        var courseRecs = attendance.getCourseAttendance("CS101");
        assertEquals(2, courseRecs.size());

        // 3. "Generate" (inspect) report values
        long present = courseRecs.stream().filter(r -> r.getStatus() == AttendanceStatus.PRESENT).count();
        long late = courseRecs.stream().filter(r -> r.getStatus() == AttendanceStatus.LATE).count();
        assertEquals(1, present);
        assertEquals(1, late);
    }

    // ---- in-memory doubles
    static class InMemAttendance implements AttendanceDAO {
        final List<AttendanceRecord> store = new ArrayList<>();
        @Override public boolean save(AttendanceRecord record) { store.add(record); return true; }
        @Override public boolean update(AttendanceRecord record) { return true; }
        @Override public List<AttendanceRecord> getStudentAttendance(String studentId, String courseId) {
            List<AttendanceRecord> list = new ArrayList<>();
            for (var r: store) if (r.getCourseId().equals(courseId) && r.getStudentId().equals(studentId)) list.add(r);
            return list;
        }
        @Override public List<AttendanceRecord> getCourseAttendance(String courseId) {
            List<AttendanceRecord> list = new ArrayList<>();
            for (var r: store) if (r.getCourseId().equals(courseId)) list.add(r);
            return list;
        }
    }
    static class InMemCourse implements CourseDAO {
        final Set<String> enrollments = new HashSet<>();
        final Set<String> auth = new HashSet<>();
        void enroll(String s, String c){ enrollments.add(s+"::"+c); }
        void authorize(String i, String c){ auth.add(i+"::"+c); }
        @Override public boolean isStudentEnrolled(String studentId, String courseId) {
            return enrollments.contains(studentId+"::"+courseId);
        }
        public boolean isInstructorAssigned(String instructorId, String courseId) {
            return auth.contains(instructorId+"::"+courseId);
        }
    }
}
