CREATE DATABASE IF NOT EXISTS sams_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sams_db;

-- Users table
CREATE TABLE users (
                       user_id VARCHAR(50) PRIMARY KEY,
                       username VARCHAR(100) UNIQUE NOT NULL,
                       email VARCHAR(150) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       role ENUM('STUDENT', 'INSTRUCTOR', 'ADMINISTRATOR') NOT NULL,
                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NOT NULL,
                       is_active BOOLEAN DEFAULT TRUE,
                       phone VARCHAR(20),
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                       INDEX idx_username (username),
                       INDEX idx_email (email),
                       INDEX idx_role (role)
);

-- Students table
CREATE TABLE students (
                          student_id VARCHAR(50) PRIMARY KEY,
                          user_id VARCHAR(50) NOT NULL,
                          program VARCHAR(100) NOT NULL,
                          semester INT NOT NULL,
                          enrollment_year INT NOT NULL,

                          FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Instructors table
CREATE TABLE instructors (
                             instructor_id VARCHAR(50) PRIMARY KEY,
                             user_id VARCHAR(50) NOT NULL,
                             department VARCHAR(100) NOT NULL,
                             employee_id VARCHAR(50) UNIQUE,

                             FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Administrators table
CREATE TABLE administrators (
                                admin_id VARCHAR(50) PRIMARY KEY,
                                user_id VARCHAR(50) NOT NULL,
                                department VARCHAR(100),
                                access_level INT DEFAULT 1,

                                FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Courses table
CREATE TABLE courses (
                         course_id VARCHAR(50) PRIMARY KEY,
                         course_code VARCHAR(20) UNIQUE NOT NULL,
                         course_name VARCHAR(200) NOT NULL,
                         instructor_id VARCHAR(50),
                         credits INT DEFAULT 3,
                         semester VARCHAR(50),
                         academic_year VARCHAR(20),
                         max_capacity INT DEFAULT 50,
                         description TEXT,

                         FOREIGN KEY (instructor_id) REFERENCES instructors(instructor_id) ON DELETE SET NULL
);

-- Enrollments table
CREATE TABLE enrollments (
                             enrollment_id VARCHAR(50) PRIMARY KEY,
                             student_id VARCHAR(50) NOT NULL,
                             course_id VARCHAR(50) NOT NULL,
                             enrollment_date DATE NOT NULL,
                             status ENUM('ACTIVE', 'DROPPED', 'COMPLETED') DEFAULT 'ACTIVE',

                             FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
                             FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
                             UNIQUE KEY unique_enrollment (student_id, course_id)
);

-- Lecture sessions table
CREATE TABLE lecture_sessions (
                                  session_id VARCHAR(50) PRIMARY KEY,
                                  course_id VARCHAR(50) NOT NULL,
                                  session_date DATE NOT NULL,
                                  session_time TIME NOT NULL,
                                  duration_minutes INT DEFAULT 60,
                                  location VARCHAR(100),
                                  topic VARCHAR(200),
                                  status ENUM('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT 'SCHEDULED',

                                  FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
);

-- Attendance records table
CREATE TABLE attendance_records (
                                    record_id VARCHAR(50) PRIMARY KEY,
                                    student_id VARCHAR(50) NOT NULL,
                                    session_id VARCHAR(50) NOT NULL,
                                    course_id VARCHAR(50) NOT NULL,
                                    instructor_id VARCHAR(50),
                                    attendance_status ENUM('PRESENT', 'ABSENT', 'LATE', 'EXCUSED') NOT NULL,
                                    marked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    remarks TEXT,

                                    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
                                    FOREIGN KEY (session_id) REFERENCES lecture_sessions(session_id) ON DELETE CASCADE,
                                    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
                                    FOREIGN KEY (instructor_id) REFERENCES instructors(instructor_id) ON DELETE SET NULL,
                                    UNIQUE KEY unique_attendance (student_id, session_id)
);