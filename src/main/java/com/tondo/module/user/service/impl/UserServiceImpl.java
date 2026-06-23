package com.tondo.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tondo.infrastructure.security.JwtUtil;
import com.tondo.module.user.entity.User;
import com.tondo.module.user.entity.dto.LoginDTO;
import com.tondo.module.user.entity.dto.RegisterDTO;
import com.tondo.module.user.mapper.UserMapper;
import com.tondo.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil1;

    @Override
    public User register(RegisterDTO dto) {
        // 检查手机号是否已注册
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, dto.getPhone());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("手机号已注册");
        }

        // 检查昵称唯一性
        wrapper.clear();
        wrapper.eq(User::getNickname, dto.getNickname());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("昵称已被占用");
        }

        User user = new User();
        user.setPhone(dto.getPhone());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setRole("USER");
        user.setTrustLevel(1);
        user.setIsFrozen(0);
        user.setSignedCommunityRule(0);
        userMapper.insert(user);
        return user;
    }

    @Override
    public String login(LoginDTO dto) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, dto.getPhone());
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new RuntimeException("手机号或密码错误");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("手机号或密码错误");
        }
        if (user.getIsFrozen() == 1) {
            throw new RuntimeException("账号已被冻结");
        }

        // JWT 生成后面会加，这里先返回占位
        return jwtUtil1.generateToken(user.getId(), user.getRole());
    }

    @Override
    public User getCurrentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user;
    }
}