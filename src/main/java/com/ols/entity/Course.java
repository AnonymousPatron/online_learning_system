package com.ols.entity;

import com.ols.common.CourseStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Entity
@Getter
@Setter
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String courseName; // 과목 이름

    @Column(columnDefinition = "TEXT")
    private String description; // 상세 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher; // 담당 선생

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseStatus status; // 과목 상태: PENDING, APPROVED, REJECTED

    @ManyToMany
    @JoinTable(
            name = "course_student",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<Student> students = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public Course(String courseName, String description, Teacher teacher, CourseStatus status) {
        this.courseName = courseName;
        this.description = description;
        this.teacher = teacher;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStatus(CourseStatus status) {
        this.status = status;
    }

}
