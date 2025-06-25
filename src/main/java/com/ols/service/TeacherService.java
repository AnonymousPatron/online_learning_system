package com.ols.service;

import com.ols.dto.request.UserSignupRequestDto;
import com.ols.entity.Teacher;
import com.ols.entity.Users;
import com.ols.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public Teacher save(Users users) {
        return teacherRepository.save(
                Teacher.builder()
                        .users(users)
                        .build()
        );
    }

    public Teacher findById(Long userId) {
        return teacherRepository.findByUsersId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + userId));
    }

}
