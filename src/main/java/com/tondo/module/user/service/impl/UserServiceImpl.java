package com.tondo.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tondo.infrastructure.security.JwtUtil;
import com.tondo.infrastructure.security.TokenBlacklistService;
import com.tondo.common.exception.BusinessException;
import com.tondo.infrastructure.storage.FileStorageService;
import com.tondo.module.user.entity.User;
import com.tondo.module.user.entity.dto.LoginDTO;
import com.tondo.module.user.entity.dto.RegisterDTO;
import com.tondo.module.user.entity.dto.UpdateProfileDTO;
import com.tondo.module.user.entity.vo.LoginTokenVO;
import com.tondo.module.user.entity.vo.UserBriefVO;
import com.tondo.module.user.mapper.UserMapper;
import com.tondo.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final FileStorageService fileStorageService;

    @Override
    public User register(RegisterDTO dto) {
        // 检查手机号是否已注册
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, dto.getPhone());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("手机号已注册");
        }

        // 检查昵称唯一性
        wrapper.clear();
        wrapper.eq(User::getNickname, dto.getNickname());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("昵称已被占用");
        }

        User user = new User();
        user.setPhone(dto.getPhone());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setRole("USER");
        user.setTrustLevel(1);
        user.setIsFrozen(0);
        user.setSignedCommunityRule(dto.isAcceptCommunityRule() ? 1 : 0);
        userMapper.insert(user);
        return user;
    }

    @Override
    public LoginTokenVO login(LoginDTO dto) {
        User user = authenticate(dto.getPhone(), dto.getPassword());
        return buildTokenPair(user);
    }

    @Override
    public LoginTokenVO refresh(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            throw new BusinessException(401, "刷新令牌无效或已过期");
        }
        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        User user = userMapper.selectById(userId);
        if (user == null || user.getIsFrozen() != null && user.getIsFrozen() == 1) {
            throw new BusinessException(401, "账号不可用");
        }
        return buildTokenPair(user);
    }

    @Override
    public void logout(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            return;
        }
        long ttl = jwtUtil.getRemainingMillis(accessToken);
        tokenBlacklistService.blacklist(accessToken, ttl);
    }

    private User authenticate(String phone, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new BusinessException("手机号或密码错误");
        }
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BusinessException("手机号或密码错误");
        }
        if (user.getIsFrozen() != null && user.getIsFrozen() == 1) {
            throw new BusinessException("账号已被冻结");
        }
        return user;
    }

    private LoginTokenVO buildTokenPair(User user) {
        String role = user.getRole() != null ? user.getRole() : "USER";
        String accessToken = jwtUtil.generateAccessToken(user.getId(), role);
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), role);
        return new LoginTokenVO(accessToken, refreshToken, jwtUtil.getAccessExpiration());
    }

    @Override
    public User getCurrentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        normalizeUserMedia(user);
        return user;
    }

    @Override
    public User updateProfile(Long userId, UpdateProfileDTO dto) {
        User user = getCurrentUser(userId);
        if (dto.getStatusLabel() != null) {
            user.setStatusLabel(dto.getStatusLabel());
        }
        if (dto.getConfusionTags() != null) {
            user.setConfusionTags(dto.getConfusionTags());
        }
        if (dto.getCompanionStyle() != null) {
            user.setCompanionStyle(dto.getCompanionStyle());
        }
        if (dto.getAvatarUrl() != null) {
            user.setAvatarUrl(dto.getAvatarUrl());
        }
        userMapper.updateById(user);
        normalizeUserMedia(user);
        return user;
    }

    private void normalizeUserMedia(User user) {
        if (user != null && user.getAvatarUrl() != null) {
            user.setAvatarUrl(fileStorageService.normalizePublicUrl(user.getAvatarUrl()));
        }
    }

    @Override
    public List<UserBriefVO> searchUsers(String keyword, Long excludeUserId) {
        if (keyword == null || keyword.isBlank()) {
            return Collections.emptyList();
        }
        String trimmed = keyword.trim();

        if (trimmed.matches("\\d+")) {
            Long userId = Long.parseLong(trimmed);
            if (!userId.equals(excludeUserId)) {
                User user = userMapper.selectById(userId);
                if (user != null && isActiveUser(user)) {
                    return List.of(toBriefVO(user));
                }
            }
        }

        String safeKeyword = sanitizeLikeKeyword(trimmed);
        if (safeKeyword.isEmpty()) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(User::getNickname, safeKeyword);
        applyActiveUserFilter(wrapper);
        wrapper.ne(excludeUserId != null, User::getId, excludeUserId)
                .last("LIMIT 20");
        return userMapper.selectList(wrapper).stream().map(this::toBriefVO).collect(Collectors.toList());
    }

    @Override
    public UserBriefVO getUserBrief(Long targetUserId, Long requesterId) {
        if (targetUserId.equals(requesterId)) {
            throw new BusinessException("不能邀请自己");
        }
        User user = userMapper.selectById(targetUserId);
        if (user == null) {
            throw new BusinessException("用户不存在，请核对 ID");
        }
        if (!isActiveUser(user)) {
            throw new BusinessException("该用户已被冻结，无法邀请");
        }
        return toBriefVO(user);
    }

    private boolean isActiveUser(User user) {
        return user.getIsFrozen() == null || user.getIsFrozen() == 0;
    }

    private void applyActiveUserFilter(LambdaQueryWrapper<User> wrapper) {
        wrapper.and(w -> w.eq(User::getIsFrozen, 0).or().isNull(User::getIsFrozen));
    }

    @Override
    public Map<Long, String> getNicknameMap(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<User> users = userMapper.selectBatchIds(userIds);
        return users.stream().collect(Collectors.toMap(User::getId, User::getNickname));
    }

    private String sanitizeLikeKeyword(String keyword) {
        return keyword.replace("\\", "").replace("%", "").replace("_", "").trim();
    }

    private UserBriefVO toBriefVO(User user) {
        UserBriefVO vo = new UserBriefVO();
        vo.setId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setAvatarUrl(fileStorageService.normalizePublicUrl(user.getAvatarUrl()));
        vo.setStatusLabel(user.getStatusLabel());
        vo.setConfusionTags(user.getConfusionTags());
        vo.setCompanionStyle(user.getCompanionStyle());
        vo.setTrustLevel(user.getTrustLevel());
        vo.setRole(user.getRole());
        vo.setIsFrozen(user.getIsFrozen());
        return vo;
    }

    @Override
    public void assertUserActive(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(401, "用户不存在");
        }
        if (user.getIsFrozen() != null && user.getIsFrozen() == 1) {
            throw new BusinessException(403, "账号已被冻结");
        }
    }

    @Override
    public void adminUpdateUserStatus(Long userId, boolean frozen) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if ("ADMIN".equals(user.getRole()) && frozen) {
            throw new BusinessException("不能冻结管理员账号");
        }
        user.setIsFrozen(frozen ? 1 : 0);
        userMapper.updateById(user);
    }

    @Override
    public void adminUpdateUserRole(Long userId, String role) {
        if (!"USER".equals(role) && !"ADMIN".equals(role)) {
            throw new BusinessException("无效的角色");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setRole(role);
        userMapper.updateById(user);
    }
}