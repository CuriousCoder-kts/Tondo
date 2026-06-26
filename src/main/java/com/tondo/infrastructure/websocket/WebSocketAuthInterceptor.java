package com.tondo.infrastructure.websocket;

import com.tondo.common.exception.BusinessException;
import com.tondo.infrastructure.security.JwtUtil;
import com.tondo.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Long userId = resolveUserId(accessor);
            if (userId == null) {
                throw new BusinessException(401, "WebSocket 认证失败，请重新登录");
            }
            userService.assertUserActive(userId);
            accessor.setUser(new StompPrincipal(userId.toString()));
            return message;
        }

        if (StompCommand.SEND.equals(accessor.getCommand())) {
            if (accessor.getUser() == null) {
                throw new BusinessException(401, "WebSocket 未认证");
            }
            Long userId = Long.valueOf(accessor.getUser().getName());
            userService.assertUserActive(userId);
        }

        return message;
    }

    private Long resolveUserId(StompHeaderAccessor accessor) {
        String token = accessor.getFirstNativeHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtUtil.validateToken(token)) {
                return jwtUtil.getUserIdFromToken(token);
            }
        }
        return null;
    }
}
