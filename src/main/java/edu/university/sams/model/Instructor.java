package edu.university.sams.model;

import edu.university.sams.model.enums.UserRole;
import java.util.Date;

public class Instructor extends User {
    private String instructorId;
    private String department;
    private String employeeId;

    public Instructor() {
        super();
    }

    public Instructor(String userId, String username, String email, String passwordHash,
                      String firstName, String lastName, String instructorId,
                      String department, String employeeId, String phone, Date updatedAt) {
        super(userId, username, email, passwordHash, UserRole.INSTRUCTOR, firstName, lastName, true, phone, updatedAt);
        this.instructorId = instructorId;
        this.department = department;
        this.employeeId = employeeId;
    }

    // Getters and Setters
    public String getInstructorId() { return instructorId; }
    public void setInstructorId(String instructorId) { this.instructorId = instructorId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    @Override
    public String toString() {
        return "Instructor{" +
                "instructorId='" + instructorId + '\'' +
                ", department='" + department + '\'' +
                ", employeeId='" + employeeId + '\'' +
                "} " + super.toString();
    }
}