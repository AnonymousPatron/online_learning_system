package com.ols.controller;

import com.ols.config.jwt.TokenProvider;
import com.ols.dto.UserLoginRequestDto;
import com.ols.dto.UserSignupRequestDto;
import com.ols.dto.jwt.LoginResponseDto;
import com.ols.entity.RefreshToken;
import com.ols.entity.RefreshTokenRequest;
import com.ols.entity.User;
import com.ols.service.UserService;
import com.ols.service.jwt.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class UserLoginApiController {

    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final TokenProvider tokenProvider;
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserSignupRequestDto requestDto) {
        try {
            userService.save(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원가입 실패");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequestDto requestDto, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = (User) authentication.getPrincipal();

            String accessToken = tokenProvider.generateAccessToken(user, Duration.ofHours(2));
            RefreshToken refreshTokenEntity = refreshTokenService.createRefreshToken(user, Duration.ofDays(1));
            String refreshToken = refreshTokenEntity.getRefreshToken();

            // HttpOnly 쿠키에 access Token 저장
            Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
//            accessTokenCookie.setDomain("localhost");
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setPath("/"); // access Token 요청을 위한 특정 경로 설정 고려
            accessTokenCookie.setMaxAge((int) Duration.ofDays(1).toSeconds()); // 쿠키 유효 시간 설정
            response.addCookie(accessTokenCookie);

            // HttpOnly 쿠키에 Refresh Token 저장
            Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
//            refreshTokenCookie.setDomain("localhost");
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setPath("/refresh"); // Refresh Token 요청을 위한 특정 경로 설정 고려
            refreshTokenCookie.setMaxAge((int) Duration.ofDays(1).toSeconds()); // 쿠키 유효 시간 설정
            response.addCookie(refreshTokenCookie);

            return ResponseEntity.ok().body(LoginResponseDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build()
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();

        if (refreshToken != null) {
            RefreshToken refreshTokenEntity = refreshTokenService.findByRefreshToken(refreshToken);
            if (tokenProvider.validToken(refreshToken) && refreshTokenEntity.getExpiryDate().isAfter(java.time.Instant.now())) {
                User user = userService.findById(refreshTokenEntity.getUserId());
                String newAccessToken = tokenProvider.generateAccessToken(user, Duration.ofHours(2));
                String newRefreshToken = tokenProvider.generateRefreshToken(user, Duration.ofDays(7)); // 필요에 따라 새 리프레시 토큰 발급 및 저장

                refreshTokenEntity.update(newRefreshToken);
                refreshTokenService.save(refreshTokenEntity);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("accessToken", newAccessToken);
                tokens.put("refreshToken", newRefreshToken);

                return ResponseEntity.ok(tokens);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
    }

}
