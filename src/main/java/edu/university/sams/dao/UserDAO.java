package edu.university.sams.dao;

import edu.university.sams.model.User;
import edu.university.sams.model.Student;
import edu.university.sams.model.Instructor;

import java.util.List;

public interface UserDAO {

    User findByUsername(String username);
    User findById(String userId);
    boolean save(User user);
    boolean update(User user);
    List<User> getAllUsers();
    List<Student> getAllStudents();
    List<Instructor> getAllInstructors();
    boolean setUserActive(String userId, boolean active);
    boolean resetPassword(String userId, String newPasswordHash);
}