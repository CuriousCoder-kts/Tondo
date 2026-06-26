package com.tondo.module.user.controller;

import com.tondo.common.constant.CommunityRuleContent;
import com.tondo.common.response.Result;
import com.tondo.common.annotation.RateLimit;
import com.tondo.module.user.entity.User;
import com.tondo.module.user.entity.dto.LoginDTO;
import com.tondo.module.user.entity.dto.RefreshTokenDTO;
import com.tondo.module.user.entity.dto.RegisterDTO;
import com.tondo.module.user.entity.dto.UpdateProfileDTO;
import com.tondo.module.user.entity.vo.CommunityRuleVO;
import com.tondo.module.user.entity.vo.LoginTokenVO;
import com.tondo.module.user.entity.vo.UserBriefVO;
import com.tondo.module.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @RateLimit(key = "register", limit = 5, seconds = 300)
    public Result<User> register(@Valid @RequestBody RegisterDTO dto) {
        User user = userService.register(dto);
        user.setPasswordHash(null);
        return Result.success(user);
    }

    @PostMapping("/login")
    @RateLimit(key = "login", limit = 10, seconds = 60)
    public Result<LoginTokenVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    @PostMapping("/refresh")
    @RateLimit(key = "refresh", limit = 30, seconds = 60)
    public Result<LoginTokenVO> refresh(@Valid @RequestBody RefreshTokenDTO dto) {
        return Result.success(userService.refresh(dto.getRefreshToken()));
    }

    @PostMapping("/logout")
    public Result<?> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            userService.logout(authorization.substring(7));
        }
        return Result.success();
    }

    @GetMapping("/me")
    public Result<User> me(@RequestAttribute(value = "userId", required = false) Long userId) {
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        User user = userService.getCurrentUser(userId);
        user.setPasswordHash(null);
        return Result.success(user);
    }

    @PutMapping("/me")
    public Result<User> updateMe(@RequestAttribute("userId") Long userId,
                                 @RequestBody UpdateProfileDTO dto) {
        User user = userService.updateProfile(userId, dto);
        user.setPasswordHash(null);
        return Result.success(user);
    }

    @GetMapping("/search")
    public Result<List<UserBriefVO>> searchUsers(@RequestAttribute("userId") Long userId,
                                                 @RequestParam(required = false) String keyword,
                                                 @RequestParam(required = false) String nickname) {
        String query = (keyword != null && !keyword.isBlank()) ? keyword : nickname;
        if (query == null || query.isBlank()) {
            return Result.error(400, "请输入用户 ID 或昵称");
        }
        return Result.success(userService.searchUsers(query, userId));
    }

    @GetMapping("/{id}/brief")
    public Result<UserBriefVO> getUserBrief(@RequestAttribute("userId") Long userId,
                                            @PathVariable Long id) {
        return Result.success(userService.getUserBrief(id, userId));
    }

    @GetMapping("/community-rule")
    public Result<CommunityRuleVO> getCommunityRule() {
        CommunityRuleVO vo = new CommunityRuleVO();
        vo.setVersion(CommunityRuleContent.VERSION);
        vo.setTitle(CommunityRuleContent.TITLE);
        vo.setContent(CommunityRuleContent.CONTENT);
        return Result.success(vo);
    }
}
