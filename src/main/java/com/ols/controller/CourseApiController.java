package com.ols.controller;

import com.ols.common.CourseStatus;
import com.ols.dto.request.CourseEnrollRequestDto;
import com.ols.dto.request.CourseRegisterRequestDto;
import com.ols.dto.response.CourseResponseDto;
import com.ols.entity.Course;
import com.ols.entity.Teacher;
import com.ols.service.CourseService;
import com.ols.service.EnrollmentService;
import com.ols.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/courses")
public class CourseApiController {

    private final TeacherService teacherService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    // courses List 전체 반환
    @GetMapping
    public ResponseEntity<List<CourseResponseDto>> viewCourses() {
        return ResponseEntity.ok(courseService.findAll());
    }

    // teacherId 기반 courses List 반환
    @GetMapping("/me")
    public ResponseEntity<List<CourseResponseDto>> viewCourses(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(courseService.findByUserId(userId));
    }

    // 선생이 강의를 등록
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Course> registerCourse(
            @RequestBody CourseRegisterRequestDto requestDto,
            @AuthenticationPrincipal Long userId
    ) {
        Teacher teacher = teacherService.findById(userId);

        if (teacher == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Course course = courseService.save(requestDto, teacher);

        return ResponseEntity.ok(course);
    }

    // 학생이 강의를 수강신청
    @PostMapping("/enroll")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Course> enrollCourse(
            @AuthenticationPrincipal Long userId,
            @RequestBody CourseEnrollRequestDto requestDto
    ) {
        return ResponseEntity.ok(enrollmentService.enrollCourse(userId, requestDto));
    }

    // update courses status
    @PatchMapping("/{courseId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Course> updateCourseStatus(
            @PathVariable Long courseId,
            @RequestBody CourseStatus status
//            @AuthenticationPrincipal Long userId
    ) {
        courseService.updateCourseStatus(courseId, status);
        return ResponseEntity.noContent().build();
    }

}
