package com.ols.controller;

import com.ols.config.jwt.TokenProvider;
import com.ols.dto.UserInfoResponseDto;
import com.ols.entity.User;
import com.ols.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class UserInfoApiController {

    private final TokenProvider tokenProvider;
    private final UserService userService;

    @GetMapping("/api/user/info")
    public ResponseEntity<UserInfoResponseDto> getCurrentUser(HttpServletRequest request) { // @CookieValue 대신 HttpServletRequest 사용
        String accessToken = getAccessTokenFromRequest(request);

        if (accessToken != null && tokenProvider.validToken(accessToken)) {
            // Access Token에서 직접 userId 가져오기
            Long userId = tokenProvider.getUserIdFromToken(accessToken);
            if (userId == null) {
                // 토큰에 userId 클레임이 없는 경우 (예외 처리)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            User user = userService.findById(userId); // ID로 사용자 찾기

//            System.out.println("username: " + user.getUsername() + ", role: " + user.getRole());
            return ResponseEntity.ok(UserInfoResponseDto.builder()
                    .username(user.getUsername())
                    .role(user.getRole())
                    .build());
        }
        System.out.println("accessToken: " + accessToken); // 디버깅용
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // 요청에서 Access Token을 가져오는 헬퍼 메서드
    private String getAccessTokenFromRequest(HttpServletRequest request) {
        // 1. Authorization 헤더에서 Bearer 토큰 추출
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring("Bearer ".length());
        }

        // 2. 없으면 "accessToken" 쿠키에서 추출
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Optional<Cookie> accessTokenCookie = Arrays.stream(cookies)
                    .filter(cookie -> "accessToken".equals(cookie.getName()))
                    .findFirst();
            if (accessTokenCookie.isPresent()) {
                return accessTokenCookie.get().getValue();
            }
        }
        return null;
    }

}
