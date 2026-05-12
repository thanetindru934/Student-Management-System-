package edu.university.sams.integration;

import edu.university.sams.dao.AttendanceDAO;
import edu.university.sams.dao.CourseDAO;
import edu.university.sams.model.AttendanceRecord;
import edu.university.sams.model.enums.AttendanceStatus;
import edu.university.sams.service.AttendanceService;
import edu.university.sams.service.ValidationService;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrencyIntegrationTest {

    @Test
    void IT_CONC_001_concurrentAccess() throws Exception {
        InMemAttendance attendance = new InMemAttendance();
        InMemCourse courses = new InMemCourse();
        courses.enroll("S1", "CS101");
        courses.enroll("S2", "CS101");
        courses.authorize("INST001", "CS101");

        AttendanceService svc = new AttendanceService(attendance, courses, new ValidationService());

        ExecutorService pool = Executors.newFixedThreadPool(2);
        Future<Boolean> f1 = pool.submit(() -> svc.markAttendance("S1", "CS101", "INST001", AttendanceStatus.PRESENT, "CS101_20991231", ""));
        Future<Boolean> f2 = pool.submit(() -> svc.markAttendance("S2", "CS101", "INST001", AttendanceStatus.PRESENT, "CS101_20991231", ""));
        assertTrue(f1.get(5, TimeUnit.SECONDS));
        assertTrue(f2.get(5, TimeUnit.SECONDS));
        pool.shutdown();

        assertEquals(2, attendance.getCourseAttendance("CS101").size());
    }

    static class InMemAttendance implements AttendanceDAO {
        final List<AttendanceRecord> store = Collections.synchronizedList(new ArrayList<>());
        @Override public boolean save(AttendanceRecord record) { store.add(record); return true; }
        @Override public boolean update(AttendanceRecord record) { return true; }
        @Override public List<AttendanceRecord> getStudentAttendance(String studentId, String courseId) {
            List<AttendanceRecord> list = new ArrayList<>();
            synchronized (store) {
                for (var r: store) if (r.getCourseId().equals(courseId) && r.getStudentId().equals(studentId)) list.add(r);
            }
            return list;
        }
        @Override public List<AttendanceRecord> getCourseAttendance(String courseId) {
            List<AttendanceRecord> list = new ArrayList<>();
            synchronized (store) {
                for (var r: store) if (r.getCourseId().equals(courseId)) list.add(r);
            }
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
