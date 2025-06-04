package com.ols.controller;

import com.ols.config.jwt.TokenProvider;
import com.ols.dto.UserInfoResponseDto;
import com.ols.entity.User;
import com.ols.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class UserInfoApiController {

    private final TokenProvider tokenProvider;
    private final UserService userService;

    @GetMapping("/api/user/info")
    public ResponseEntity<UserInfoResponseDto> getCurrentUser(
            @CookieValue(name = "accessToken", required = false) String accessToken) {
        System.out.println("getCurrentUser");
        if (accessToken != null && tokenProvider.validToken(accessToken)) {
            String userId = tokenProvider.getUserIdFromToken(accessToken);
            User user = userService.findById(Long.parseLong(userId));

            System.out.println("username: " + user.getUsername() + ", role: " + user.getRole());
            return ResponseEntity.ok(UserInfoResponseDto.builder()
                            .username(user.getUsername())
                            .role(user.getRole())
                            .build());
        }
        System.out.println("accessToken: " + accessToken);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
