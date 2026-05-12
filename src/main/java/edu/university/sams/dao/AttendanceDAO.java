package edu.university.sams.dao;

import edu.university.sams.model.AttendanceRecord;
import java.util.List;
import java.util.Map;

public interface AttendanceDAO {
    boolean save(AttendanceRecord record);
    boolean update(AttendanceRecord record);
    AttendanceRecord findById(String recordId);
    List<AttendanceRecord> getStudentAttendance(String studentId, String courseId);
    boolean existsAttendance(String studentId, String sessionId);
    Map<String, Object> getAttendanceStatistics(String studentId, String courseId);
    List<AttendanceRecord> getCourseAttendance(String courseId);
    boolean delete(String recordId);
}
