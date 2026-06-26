package com.tondo.module.user.service;

import com.tondo.module.user.entity.User;
import com.tondo.module.user.entity.dto.LoginDTO;
import com.tondo.module.user.entity.dto.RegisterDTO;
import com.tondo.module.user.entity.dto.UpdateProfileDTO;
import com.tondo.module.user.entity.vo.LoginTokenVO;
import com.tondo.module.user.entity.vo.UserBriefVO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UserService {
    User register(RegisterDTO dto);

    LoginTokenVO login(LoginDTO dto);

    LoginTokenVO refresh(String refreshToken);

    void logout(String accessToken);

    User getCurrentUser(Long userId);

    User updateProfile(Long userId, UpdateProfileDTO dto);

    List<UserBriefVO> searchUsers(String keyword, Long excludeUserId);

    UserBriefVO getUserBrief(Long targetUserId, Long requesterId);

    Map<Long, String> getNicknameMap(Collection<Long> userIds);

    void assertUserActive(Long userId);

    void adminUpdateUserStatus(Long userId, boolean frozen);

    void adminUpdateUserRole(Long userId, String role);
}
