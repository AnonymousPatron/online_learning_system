package com.ols.controller;

import com.ols.dto.response.UserInfoResponseDto;
import com.ols.entity.Users;
import com.ols.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserApiController {

    private final UserService userService;

    @GetMapping("/info")
    public ResponseEntity<UserInfoResponseDto> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = (Long) authentication.getPrincipal();

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Users users = userService.findById(userId); // ID로 사용자 찾기

        if (users == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(UserInfoResponseDto.builder()
                .username(users.getUsername())
                .role(users.getRole())
                .build()
        );
    }

}
