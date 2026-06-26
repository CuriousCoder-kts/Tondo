package com.tondo.module.user.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginTokenVO {
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
}
