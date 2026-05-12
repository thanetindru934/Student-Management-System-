USE sams_db;

-- Clear existing data
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE attendance_records;
TRUNCATE TABLE lecture_sessions;
TRUNCATE TABLE enrollments;
TRUNCATE TABLE courses;
TRUNCATE TABLE administrators;
TRUNCATE TABLE instructors;
TRUNCATE TABLE students;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- Insert users with plain password for testing
INSERT INTO users (user_id, username, email, password_hash, role, first_name, last_name, is_active, phone) VALUES
                                                                                                               ('USR001', 'admin', 'admin@sams.edu', 'password', 'ADMINISTRATOR', 'System', 'Admin', TRUE, '123-456-7890'),
                                                                                                               ('USR002', 'john.doe', 'john.doe@sams.edu', 'password', 'INSTRUCTOR', 'John', 'Doe', TRUE, '123-456-7891'),
                                                                                                               ('USR003', 'jane.smith', 'jane.smith@student.sams.edu', 'password', 'STUDENT', 'Jane', 'Smith', TRUE, '123-456-7892'),
                                                                                                               ('USR004', 'bob.johnson', 'bob.johnson@student.sams.edu', 'password', 'STUDENT', 'Bob', 'Johnson', TRUE, '123-456-7893'),
                                                                                                               ('USR005', 'alice.brown', 'alice.brown@sams.edu', 'password', 'INSTRUCTOR', 'Alice', 'Brown', TRUE, '123-456-7894');

-- Insert administrators
INSERT INTO administrators (admin_id, user_id, department, access_level) VALUES
    ('ADM001', 'USR001', 'IT Administration', 3);

-- Insert instructors
INSERT INTO instructors (instructor_id, user_id, department, employee_id) VALUES
                                                                              ('INS001', 'USR002', 'Computer Science', 'EMP001'),
                                                                              ('INS002', 'USR005', 'Mathematics', 'EMP002');

-- Insert students
INSERT INTO students (student_id, user_id, program, semester, enrollment_year) VALUES
                                                                                   ('STU001', 'USR003', 'Computer Science', 5, 2023),
                                                                                   ('STU002', 'USR004', 'Computer Science', 3, 2024);

-- Insert courses
INSERT INTO courses (course_id, course_code, course_name, instructor_id, credits, semester, academic_year, max_capacity, description) VALUES
                                                                                                                                          ('CSE101', 'CS101', 'Introduction to Programming', 'INS001', 3, 'Fall 2024', '2024-25', 50, 'Basic programming concepts using Java'),
                                                                                                                                          ('CSE201', 'CS201', 'Data Structures', 'INS001', 4, 'Spring 2025', '2024-25', 40, 'Fundamental data structures and algorithms'),
                                                                                                                                          ('MATH101', 'MATH101', 'Calculus I', 'INS002', 4, 'Fall 2024', '2024-25', 60, 'Differential and integral calculus');

-- Insert enrollments
INSERT INTO enrollments (enrollment_id, student_id, course_id, enrollment_date, status) VALUES
                                                                                            ('ENR001', 'STU001', 'CSE101', '2024-09-01', 'ACTIVE'),
                                                                                            ('ENR002', 'STU001', 'CSE201', '2024-09-01', 'ACTIVE'),
                                                                                            ('ENR003', 'STU002', 'CSE101', '2024-09-01', 'ACTIVE'),
                                                                                            ('ENR004', 'STU002', 'MATH101', '2024-09-01', 'ACTIVE');

-- Insert lecture sessions
INSERT INTO lecture_sessions (session_id, course_id, session_date, session_time, duration_minutes, location, topic, status) VALUES
                                                                                                                                ('SES001', 'CSE101', '2024-12-01', '10:00:00', 60, 'Room 101', 'Introduction to Java', 'COMPLETED'),
                                                                                                                                ('SES002', 'CSE101', '2024-12-03', '10:00:00', 60, 'Room 101', 'Variables and Data Types', 'COMPLETED'),
                                                                                                                                ('SES003', 'CSE101', '2024-12-05', '10:00:00', 60, 'Room 101', 'Control Structures', 'COMPLETED'),
                                                                                                                                ('SES004', 'CSE201', '2024-12-02', '14:00:00', 90, 'Room 201', 'Arrays and Lists', 'COMPLETED'),
                                                                                                                                ('SES005', 'MATH101', '2024-12-01', '09:00:00', 60, 'Room 301', 'Limits and Continuity', 'COMPLETED');

-- Insert attendance records
INSERT INTO attendance_records (record_id, student_id, session_id, course_id, instructor_id, attendance_status, remarks) VALUES
                                                                                                                             ('REC001', 'STU001', 'SES001', 'CSE101', 'INS001', 'PRESENT', 'Active participation'),
                                                                                                                             ('REC002', 'STU002', 'SES001', 'CSE101', 'INS001', 'PRESENT', ''),
                                                                                                                             ('REC003', 'STU001', 'SES002', 'CSE101', 'INS001', 'LATE', 'Arrived 10 minutes late'),
                                                                                                                             ('REC004', 'STU002', 'SES002', 'CSE101', 'INS001', 'ABSENT', 'Sick leave'),
                                                                                                                             ('REC005', 'STU001', 'SES004', 'CSE201', 'INS001', 'PRESENT', ''),
                                                                                                                             ('REC006', 'STU002', 'SES005', 'MATH101', 'INS002', 'PRESENT', '');

COMMIT;