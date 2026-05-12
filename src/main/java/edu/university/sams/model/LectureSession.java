package edu.university.sams.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class LectureSession {
    private String sessionId;
    private String courseId;
    private LocalDate sessionDate;
    private LocalTime sessionTime;
    private int durationMinutes;
    private String location;
    private String topic;
    private String status;

    public LectureSession() {}

    public LectureSession(String sessionId, String courseId, LocalDate sessionDate,
                          LocalTime sessionTime, int durationMinutes) {
        this.sessionId = sessionId;
        this.courseId = courseId;
        this.sessionDate = sessionDate;
        this.sessionTime = sessionTime;
        this.durationMinutes = durationMinutes;
        this.status = "SCHEDULED";
    }

    // Getters and Setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }

    public LocalTime getSessionTime() { return sessionTime; }
    public void setSessionTime(LocalTime sessionTime) { this.sessionTime = sessionTime; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return sessionId + " - " + sessionDate + " " + sessionTime +
                (topic != null ? " (" + topic + ")" : "");
    }
}
