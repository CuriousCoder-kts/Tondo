package com.tondo.module.card.controller;

import com.tondo.common.response.Result;
import com.tondo.module.card.entity.dto.CreateReplyDTO;
import com.tondo.module.card.entity.vo.ReplyVO;
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

    @PostMapping
    public Result<ReplyVO> createReply(@RequestAttribute("userId") Long userId,
                                     @PathVariable Long cardId,
                                     @Valid @RequestBody CreateReplyDTO dto) {
        return Result.success(replyService.createReply(userId, cardId, dto));
    }

    @GetMapping
    public Result<List<ReplyVO>> getReplies(@PathVariable Long cardId) {
        return Result.success(replyService.getReplyVOsByCardId(cardId));
    }

    @PostMapping("/{replyId}/thanks")
    public Result<?> thankReply(@RequestAttribute("userId") Long userId,
                                @PathVariable Long replyId) {
        replyService.thankReply(userId, replyId);
        return Result.success();
    }
}
