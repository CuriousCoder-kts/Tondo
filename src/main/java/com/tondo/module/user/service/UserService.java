package com.tondo.module.user.service;

import com.tondo.module.user.entity.User;
import com.tondo.module.user.entity.dto.LoginDTO;
import com.tondo.module.user.entity.dto.RegisterDTO;

public interface UserService {
    User register(RegisterDTO dto);
    String login(LoginDTO dto);
    User getCurrentUser(Long userId);
}