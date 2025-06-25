package com.ols.dto.response;

import com.ols.common.CourseStatus;
import com.ols.entity.Course;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CourseResponseDto {

    private Long courseId;
    private String courseName;
    private String description;
    private String teacherName;

    private CourseStatus courseStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public CourseResponseDto(
            Long courseId,
            String courseName,
            String description,
            String teacherName,
            CourseStatus courseStatus,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.description = description;
        this.teacherName = teacherName;
        this.courseStatus = courseStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CourseResponseDto fromEntity(Course course, String teacherName) {
        return CourseResponseDto.builder()
                .courseId(course.getId())
                .courseName(course.getCourseName())
                .description(course.getDescription())
                .teacherName(teacherName)
                .courseStatus(course.getStatus())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }

}
