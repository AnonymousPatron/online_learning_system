package com.ols.dto.jwt;

import lombok.Getter;

@Getter
public class AccessTokenRequestDto {
    private String refreshToken;
}
