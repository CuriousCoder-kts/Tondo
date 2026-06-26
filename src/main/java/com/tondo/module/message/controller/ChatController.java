package com.tondo.module.message.controller;

import com.tondo.module.message.entity.PrivateMessage;
import com.tondo.module.message.service.PrivateMessageService;
import com.tondo.module.notification.entity.vo.NotificationVO;
import com.tondo.module.notification.service.NotificationService;
import com.tondo.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final PrivateMessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;
    private final UserService userService;

    // 接收私聊消息
    @MessageMapping("/chat.private")
    public void handlePrivateMessage(@Payload ChatMessagePayload payload, Principal principal) {
        Long senderId = Long.valueOf(principal.getName());
        // 保存消息并获取完整的消息对象
        PrivateMessage savedMessage = messageService.sendMessage(senderId, payload.getRelationId(), payload.getContent());

        // 推送给接收者
        messagingTemplate.convertAndSendToUser(
                savedMessage.getReceiverId().toString(),
                "/queue/private",
                savedMessage
        );

        String senderName = userService.getNicknameMap(java.util.List.of(senderId))
                .getOrDefault(senderId, "用户" + senderId);
        NotificationVO notification = new NotificationVO();
        notification.setType("NEW_MESSAGE");
        notification.setTitle("新私聊消息");
        notification.setContent(senderName + "：" + truncate(savedMessage.getContent(), 40));
        notification.setRelationId(savedMessage.getRelationId());
        notification.setSenderId(senderId);
        notificationService.send(savedMessage.getReceiverId(), notification);
    }

    private String truncate(String text, int maxLen) {
        if (text == null) {
            return "";
        }
        return text.length() <= maxLen ? text : text.substring(0, maxLen) + "…";
    }

    // 内部类用于接收消息体
    @lombok.Data
    public static class ChatMessagePayload {
        private Long relationId;
        private String content;
    }
}