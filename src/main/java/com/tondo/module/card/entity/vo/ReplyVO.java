package com.tondo.module.card.entity.vo;

import com.tondo.module.card.entity.Reply;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReplyVO {
    private Long id;
    private Long cardId;
    private Long userId;
    private String authorNickname;
    private Long parentId;
    private String experienceSituation;
    private String experienceAction;
    private String experienceResult;
    private String replyType;
    private Integer thanksCount;
    private Integer isHidden;
    private LocalDateTime createdAt;

    public static ReplyVO from(Reply reply, String authorNickname) {
        ReplyVO vo = new ReplyVO();
        vo.setId(reply.getId());
        vo.setCardId(reply.getCardId());
        vo.setUserId(reply.getUserId());
        vo.setAuthorNickname(authorNickname);
        vo.setParentId(reply.getParentId());
        vo.setExperienceSituation(reply.getExperienceSituation());
        vo.setExperienceAction(reply.getExperienceAction());
        vo.setExperienceResult(reply.getExperienceResult());
        vo.setReplyType(reply.getReplyType());
        vo.setThanksCount(reply.getThanksCount());
        vo.setIsHidden(reply.getIsHidden());
        vo.setCreatedAt(reply.getCreatedAt());
        return vo;
    }
}
