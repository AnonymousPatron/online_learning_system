package com.ols.dto;

import com.ols.common.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoResponseDto {

    private String username;
    private Role role;

    @Builder
    public UserInfoResponseDto(String username, Role role) {
        this.username = username;
        this.role = role;
    }

}
