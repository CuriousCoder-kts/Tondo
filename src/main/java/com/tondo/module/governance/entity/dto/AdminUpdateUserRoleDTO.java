package com.tondo.module.governance.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminUpdateUserRoleDTO {
    @NotBlank
    private String role;
}
