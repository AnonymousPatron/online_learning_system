package com.ols.service;

import com.ols.common.CourseStatus;
import com.ols.common.Role;
import com.ols.dto.request.CourseRegisterRequestDto;
import com.ols.dto.response.CourseResponseDto;
import com.ols.entity.Course;
import com.ols.entity.Student;
import com.ols.entity.Teacher;
import com.ols.entity.Users;
import com.ols.exception.NotFoundUsers;
import com.ols.repository.CourseRepository;
import com.ols.repository.StudentRepository;
import com.ols.repository.TeacherRepository;
import com.ols.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final UsersRepository usersRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public List<CourseResponseDto> findByUserId(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUsers("user not found"));
        List<Course> courses;

        if (user.getRole().equals(Role.TEACHER)) { // 선생일 경우 본인에 해당하는 과목만
            Teacher teacher = teacherRepository.findByUsersId(userId)
                    .orElseThrow(() -> new NotFoundUsers("teacher not found"));
            courses = courseRepository.findByTeacher(teacher);
        }
        else if (user.getRole().equals(Role.STUDENT)) { // 학생일 경우 본인에 해당하는 과목만
            Student student = studentRepository.findByUsersId(userId)
                    .orElseThrow(() -> new NotFoundUsers("student not found"));
            courses = student.getEnrolledCourses();
        }
        else { // 관리자일 경우 모든 과목
            courses = courseRepository.findAll();
        }

        return courses.stream()
                .map((course) -> {
                    Teacher teacher = course.getTeacher();

                    Users tmp_user = usersRepository.findByTeacherId(teacher.getId())
                            .orElseThrow(() -> new NotFoundUsers("teacher not found"));

                    return CourseResponseDto.fromEntity(course, tmp_user.getUsername());
                })
                .collect(Collectors.toList());
    }

    public List<CourseResponseDto> findAll() {
        List<Course> courses = courseRepository.findAll();

        return courses.stream()
                .map((course) -> {
                    Long teacherId = course.getTeacher().getId();

                    Users user = usersRepository.findByTeacherId(teacherId)
                            .orElseThrow(() -> new NotFoundUsers("user not found"));

                    return CourseResponseDto.fromEntity(course, user.getUsername());
                })
                .collect(Collectors.toList());
    }

    public Course save(CourseRegisterRequestDto requestDto, Teacher teacher) {
        return courseRepository.save(
                Course.builder()
                        .courseName(requestDto.getCourseName())
                        .description(requestDto.getDescription())
                        .teacher(teacher)
                        .status(CourseStatus.PENDING)
                        .build()
        );
    }

    @Transactional
    public Course updateCourseStatus(Long courseId, CourseStatus status) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("course Not found"));

        course.setStatus(status);
        return course;
    }

}
