package com.ols.service;

import com.ols.dto.request.CourseEnrollRequestDto;
import com.ols.entity.Course;
import com.ols.entity.Student;
import com.ols.exception.NotFoundCourse;
import com.ols.exception.NotFoundUsers;
import com.ols.repository.CourseRepository;
import com.ols.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EnrollmentService {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    public Course enrollCourse(Long userId, CourseEnrollRequestDto requestDto) {
        Course course = courseRepository.findById(requestDto.getCourseId())
                .orElseThrow(() -> new NotFoundCourse("Course not found"));
        Student student = studentRepository.findByUsersId(userId)
                .orElseThrow(() -> new NotFoundUsers("Student not found"));

        if (!course.getStudents().contains(student)) {
            course.getStudents().add(student);
            student.getEnrolledCourses().add(course);
        }
        else {
            throw new IllegalStateException("Student is already enrolled");
        }

        return courseRepository.save(course);
    }

}
