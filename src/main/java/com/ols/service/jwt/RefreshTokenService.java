package com.ols.service.jwt;

import com.ols.config.jwt.JwtProperties;
import com.ols.entity.RefreshToken;
import com.ols.entity.User;
import com.ols.repository.RefreshTokenRepository;
import com.ols.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public RefreshToken saveOrUpdateRefreshToken(Long userId, String newRefreshToken, Duration expiration) {
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUserId(userId);
        Instant expiryDate = Instant.now().plus(expiration);

        if (existingToken.isPresent()) {
            // 기존 토큰이 있으면 업데이트
            RefreshToken token = existingToken.get();
            token.update(newRefreshToken, expiryDate); // 만료 시간도 업데이트
            return refreshTokenRepository.save(token);
        } else {
            // 없으면 새로 생성
            return refreshTokenRepository.save(RefreshToken.builder()
                    .userId(userId)
                    .refreshToken(newRefreshToken)
                    .expiryDate(expiryDate)
                    .build());
        }
    }

    @Transactional(readOnly = true)
    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElse(null); // 없으면 null 반환
    }

    @Transactional
    public void deleteRefreshToken(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Transactional
    public RefreshToken updateRefreshToken(Long userId, String newRefreshToken, Duration expiration) {
        // 기존 토큰 찾기 (없으면 예외 발생 또는 새로 생성 로직)
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found for user: " + userId));

        // Refresh Token Rotation: 이전 토큰 무효화 및 새 토큰으로 업데이트
        // 여기서는 그냥 새 토큰으로 덮어쓰고, 만료 시간을 갱신합니다.
        // 더 복잡한 재사용 감지는 여기에 추가 로직 필요 (예: 이전 토큰을 블랙리스트에 추가 등)
        refreshToken.update(newRefreshToken, Instant.now().plus(expiration));
        return refreshTokenRepository.save(refreshToken);
    }

}
