package edu.university.sams.service.exceptions;

public class AttendanceWindowClosedException extends RuntimeException {
    public AttendanceWindowClosedException(String message) {
        super(message);
    }
}
