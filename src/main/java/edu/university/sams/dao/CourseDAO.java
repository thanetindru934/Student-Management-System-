package edu.university.sams.dao;

import edu.university.sams.model.Course;
import edu.university.sams.model.Student;
import java.util.List;

/**
 * Data Access Object interface for Course operations
 */
public interface CourseDAO {

    /**
     * Find course by ID
     * @param courseId Course ID to search
     * @return Course object if found, null otherwise
     */
    Course findById(String courseId);

    /**
     * Get all courses
     * @return List of all courses
     */
    List<Course> getAllCourses();

    /**
     * Get courses taught by instructor
     * @param instructorId Instructor ID
     * @return List of courses
     */
    List<Course> getCoursesByInstructor(String instructorId);

    /**
     * Get courses enrolled by student
     * @param studentId Student ID
     * @return List of courses
     */
    List<Course> getCoursesByStudent(String studentId);

    /**
     * Get students enrolled in a course
     * @param courseId Course ID
     * @return List of students
     */
    List<Student> getEnrolledStudents(String courseId);

    /**
     * Check if student is enrolled in course
     * @param studentId Student ID
     * @param courseId Course ID
     * @return true if enrolled, false otherwise
     */
    boolean isStudentEnrolled(String studentId, String courseId);

    /**
     * Save new course
     * @param course Course to save
     * @return true if successful, false otherwise
     */
    boolean save(Course course);

    /**
     * Update existing course
     * @param course Course to update
     * @return true if successful, false otherwise
     */
    boolean update(Course course);

    /**
     * Delete course
     * @param courseId Course ID to delete
     * @return true if successful, false otherwise
     */
    boolean delete(String courseId);
}
