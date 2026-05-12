package edu.university.sams.service;

import edu.university.sams.model.enums.AttendanceStatus;

import java.util.regex.Pattern;
import java.util.logging.Logger;

public class ValidationService {

    private static final Logger LOGGER = Logger.getLogger(ValidationService.class.getName());

    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^STU\\d{3,6}$");
    private static final Pattern COURSE_ID_PATTERN = Pattern.compile("^[A-Z]{3}\\d{3}$");
    private static final Pattern INSTRUCTOR_ID_PATTERN = Pattern.compile("^INS\\d{3,6}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._]{3,20}$");

    public boolean validateAttendanceInput(String studentId, String courseId,
                                           String instructorId, AttendanceStatus status) {
        return validateStudentId(studentId) &&
                validateCourseId(courseId) &&
                validateInstructorId(instructorId) &&
                status != null;
    }

    public boolean validateStudentId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            LOGGER.warning("Student ID is null or empty");
            return false;
        }

        boolean isValid = STUDENT_ID_PATTERN.matcher(studentId).matches();
        if (!isValid) {
            LOGGER.warning("Invalid student ID format: " + studentId);
        }
        return isValid;
    }

    public boolean validateCourseId(String courseId) {
        if (courseId == null || courseId.trim().isEmpty()) {
            LOGGER.warning("Course ID is null or empty");
            return false;
        }

        boolean isValid = COURSE_ID_PATTERN.matcher(courseId).matches();
        if (!isValid) {
            LOGGER.warning("Invalid course ID format: " + courseId);
        }
        return isValid;
    }

    public boolean validateInstructorId(String instructorId) {
        if (instructorId == null || instructorId.trim().isEmpty()) {
            LOGGER.warning("Instructor ID is null or empty");
            return false;
        }

        boolean isValid = INSTRUCTOR_ID_PATTERN.matcher(instructorId).matches();
        if (!isValid) {
            LOGGER.warning("Invalid instructor ID format: " + instructorId);
        }
        return isValid;
    }

    public boolean validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            LOGGER.warning("Email is null or empty");
            return false;
        }

        boolean isValid = EMAIL_PATTERN.matcher(email).matches();
        if (!isValid) {
            LOGGER.warning("Invalid email format: " + email);
        }
        return isValid;
    }

    public boolean validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            LOGGER.warning("Username is null or empty");
            return false;
        }

        boolean isValid = USERNAME_PATTERN.matcher(username).matches();
        if (!isValid) {
            LOGGER.warning("Invalid username format: " + username);
        }
        return isValid;
    }

    public boolean validatePassword(String password) {
        if (password == null || password.length() < 8) {
            LOGGER.warning("Password is too short (minimum 8 characters)");
            return false;
        }

        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);

        if (!(hasDigit && hasLower && hasUpper)) {
            LOGGER.warning("Password must contain at least one digit, one lowercase and one uppercase letter");
            return false;
        }

        return true;
    }

    public boolean validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        return name.matches("^[a-zA-Z\\s]{2,50}$");
    }

    public boolean validateNumericRange(String value, int min, int max) {
        try {
            int numValue = Integer.parseInt(value);
            return numValue >= min && numValue <= max;
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid numeric value: " + value);
            return false;
        }
    }

    public boolean validateDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            return false;
        }

        try {
            java.time.LocalDate.parse(date);
            return true;
        } catch (java.time.format.DateTimeParseException e) {
            LOGGER.warning("Invalid date format: " + date);
            return false;
        }
    }

    public boolean validateTime(String time) {
        if (time == null || time.trim().isEmpty()) {
            return false;
        }

        try {
            java.time.LocalTime.parse(time);
            return true;
        } catch (java.time.format.DateTimeParseException e) {
            LOGGER.warning("Invalid time format: " + time);
            return false;
        }
    }
}
