package com.ols.service;

import com.ols.dto.request.UserSignupRequestDto;
import com.ols.entity.Users;
import com.ols.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Users save(UserSignupRequestDto dto) {
        return usersRepository.save(
                Users.builder()
                    .username(dto.getUsername())
                    .email(dto.getEmail())
                    .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                    .role(dto.getRole())
                    .build()
        );
    }

    public Users findById(Long userId) {
        return usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public Users findByEmail(String email) {
        return usersRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user: email"));
    }

}
