package edu.university.sams.model;

import edu.university.sams.model.enums.AttendanceStatus;
import java.time.LocalDateTime;

public class AttendanceRecord {
    private String recordId;
    private String studentId;
    private String sessionId;
    private String courseId;
    private String instructorId;
    private AttendanceStatus status;
    private LocalDateTime markedAt;
    private String remarks;

    public AttendanceRecord() {}

    // Getters and Setters
    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getInstructorId() { return instructorId; }
    public void setInstructorId(String instructorId) { this.instructorId = instructorId; }

    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }

    public LocalDateTime getMarkedAt() { return markedAt; }
    public void setMarkedAt(LocalDateTime markedAt) { this.markedAt = markedAt; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    @Override
    public String toString() {
        return "AttendanceRecord{" +
                "recordId='" + recordId + '\'' +
                ", studentId='" + studentId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", status=" + status +
                ", markedAt=" + markedAt +
                '}';
    }
}