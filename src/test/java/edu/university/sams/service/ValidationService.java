package edu.university.sams.service;

import edu.university.sams.model.enums.AttendanceStatus;

/**
 * Minimal test-side ValidationService so AttendanceService tests compile
 * even if the production ValidationService differs.
 */
public class ValidationService {
    public boolean validateAttendanceInput(String studentId, String courseId, String instructorId, AttendanceStatus status) {
        return studentId != null && !studentId.isBlank()
                && courseId != null && !courseId.isBlank()
                && instructorId != null && !instructorId.isBlank()
                && status != null;
    }
}
