package com.tondo.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tondo.common.response.Result;
import com.tondo.module.user.entity.User;
import com.tondo.module.user.mapper.UserMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        boolean isLogout = "POST".equalsIgnoreCase(request.getMethod())
                && request.getRequestURI().endsWith("/user/logout");

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (!isLogout && tokenBlacklistService.isBlacklisted(token)) {
                writeJson(response, HttpServletResponse.SC_UNAUTHORIZED,
                        Result.error(401, "登录已失效，请重新登录"));
                return;
            }
            if (jwtUtil.validateToken(token) && jwtUtil.isAccessToken(token)) {
                Long userId = jwtUtil.getUserIdFromToken(token);
                User user = userMapper.selectById(userId);
                if (user != null && user.getIsFrozen() != null && user.getIsFrozen() == 1) {
                    writeJson(response, HttpServletResponse.SC_FORBIDDEN,
                            Result.error(403, "账号已被冻结"));
                    return;
                }
                if (user != null) {
                    String role = user.getRole() != null ? user.getRole() : "USER";
                    request.setAttribute("userId", userId);
                    MDC.put("userId", userId.toString());

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(userId, null,
                                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private void writeJson(HttpServletResponse response, int status, Result<?> body) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
