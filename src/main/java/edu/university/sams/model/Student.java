package edu.university.sams.model;

import edu.university.sams.model.enums.UserRole;
import java.util.Date;

public class Student extends User {
    private String studentId;
    private String program;
    private int semester;
    private int enrollmentYear;

    public Student() {
        super();
    }

    public Student(String userId, String username, String email, String passwordHash,
                   String firstName, String lastName, String studentId, String program,
                   int semester, int enrollmentYear, String phone, Date updatedAt) {
        super(userId, username, email, passwordHash, UserRole.STUDENT, firstName, lastName, true, phone, updatedAt);
        this.studentId = studentId;
        this.program = program;
        this.semester = semester;
        this.enrollmentYear = enrollmentYear;
    }

    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public int getEnrollmentYear() { return enrollmentYear; }
    public void setEnrollmentYear(int enrollmentYear) { this.enrollmentYear = enrollmentYear; }

    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", program='" + program + '\'' +
                ", semester=" + semester +
                ", enrollmentYear=" + enrollmentYear +
                "} " + super.toString();
    }
}