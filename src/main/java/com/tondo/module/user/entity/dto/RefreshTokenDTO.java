package com.tondo.module.user.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenDTO {
    @NotBlank
    private String refreshToken;
}
