package com.tondo.module.card.controller;

import com.tondo.common.response.Result;
import com.tondo.module.card.entity.Reply;
import com.tondo.module.card.entity.dto.CreateReplyDTO;
import com.tondo.module.card.service.ReplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards/{cardId}/replies")
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    // 回复卡片
    @PostMapping
    public Result<Reply> createReply(@RequestAttribute("userId") Long userId,
                                     @PathVariable Long cardId,
                                     @Valid @RequestBody CreateReplyDTO dto) {
        Reply reply = replyService.createReply(userId, cardId, dto);
        return Result.success(reply);
    }

    // 获取卡片的所有回复
    @GetMapping
    public Result<List<Reply>> getReplies(@PathVariable Long cardId) {
        List<Reply> replies = replyService.getRepliesByCardId(cardId);
        return Result.success(replies);
    }

    // 感谢回复
    @PostMapping("/{replyId}/thanks")
    public Result<?> thankReply(@RequestAttribute("userId") Long userId,
                                @PathVariable Long replyId) {
        replyService.thankReply(userId, replyId);
        return Result.success();
    }
}