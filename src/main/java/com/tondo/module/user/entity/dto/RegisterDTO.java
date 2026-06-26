package com.tondo.module.user.entity.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterDTO {
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    @AssertTrue(message = "请阅读并同意社区公约")
    private boolean acceptCommunityRule;
}
