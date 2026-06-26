package com.tondo.module.message.controller;

import com.tondo.common.response.PageResult;
import com.tondo.common.response.Result;
import com.tondo.module.message.entity.PrivateMessage;
import com.tondo.module.message.service.PrivateMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final PrivateMessageService messageService;

    @GetMapping("/{relationId}")
    public Result<PageResult<PrivateMessage>> getMessages(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long relationId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size) {
        return Result.success(messageService.getMessagesPage(userId, relationId, page, size));
    }
}
