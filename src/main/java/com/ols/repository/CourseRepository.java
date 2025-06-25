package com.ols.repository;

import com.ols.entity.Course;
import com.ols.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByTeacher(Teacher teacher);
}
