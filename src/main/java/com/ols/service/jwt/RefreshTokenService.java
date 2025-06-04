package com.ols.service.jwt;

import com.ols.config.jwt.JwtProperties;
import com.ols.entity.RefreshToken;
import com.ols.entity.User;
import com.ols.repository.RefreshTokenRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }

    public RefreshToken createRefreshToken(User user, Duration duration) {
        Optional<RefreshToken> _refreshToken = refreshTokenRepository.findByUserId(user.getId());

        if (_refreshToken.isPresent()) {
            refreshTokenRepository.delete(_refreshToken.get());
        }

        Instant expiryInstant = Instant.now().plus(duration);
        String token = generateRefreshToken(user, expiryInstant);

        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(token)
                .userId(user.getId())
                .expiryDate(expiryInstant)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    private String generateRefreshToken(User user, Instant expiryInstant) {
        Date now = Date.from(Instant.now());
        Date expiryDate = Date.from(expiryInstant);

        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .claim("id", user.getId())
                .signWith(Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes()))
                .compact();
    }

}
