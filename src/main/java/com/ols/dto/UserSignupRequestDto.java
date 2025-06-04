package com.ols.dto;

import com.ols.common.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignupRequestDto {

    private String username;
    private String email;
    private String password;
    private Role role;

}
