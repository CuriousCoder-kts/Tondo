package com.tondo.module.governance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tondo.common.exception.BusinessException;
import com.tondo.module.card.entity.Card;
import com.tondo.module.card.entity.Reply;
import com.tondo.module.card.mapper.CardMapper;
import com.tondo.module.card.mapper.ReplyMapper;
import com.tondo.module.governance.entity.ReportRecord;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tondo.common.response.PageResult;
import com.tondo.module.governance.entity.dto.CreateReportDTO;
import com.tondo.module.governance.entity.dto.HandleReportDTO;
import com.tondo.module.governance.entity.vo.ReportVO;
import com.tondo.module.governance.mapper.ReportRecordMapper;
import com.tondo.module.governance.service.ReportService;
import com.tondo.module.companion.entity.CompanionRelation;
import com.tondo.module.companion.mapper.CompanionRelationMapper;
import com.tondo.module.message.entity.PrivateMessage;
import com.tondo.module.message.mapper.PrivateMessageMapper;
import com.tondo.module.user.entity.User;
import com.tondo.module.user.mapper.UserMapper;
import com.tondo.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private static final Set<String> TARGET_TYPES = Set.of("CARD", "REPLY", "MESSAGE", "USER");
    private static final Set<String> REASONS = Set.of("HARASSMENT", "FAKE_INFO", "HATE_SPEECH", "OTHER");
    private static final Set<String> ACTIONS = Set.of("DISMISS", "HIDE_CONTENT", "FREEZE_USER");

    private final ReportRecordMapper reportMapper;
    private final CardMapper cardMapper;
    private final ReplyMapper replyMapper;
    private final PrivateMessageMapper messageMapper;
    private final CompanionRelationMapper relationMapper;
    private final UserMapper userMapper;
    private final UserService userService;

    @Override
    @Transactional
    public void createReport(Long reporterId, CreateReportDTO dto) {
        validateTargetType(dto.getTargetType());
        if (!REASONS.contains(dto.getReason())) {
            throw new BusinessException("无效的举报原因");
        }

        Long targetOwnerId = validateTargetExists(dto.getTargetType(), dto.getTargetId(), reporterId);
        if (reporterId.equals(targetOwnerId)) {
            throw new BusinessException("不能举报自己的内容");
        }

        LambdaQueryWrapper<ReportRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportRecord::getReporterId, reporterId)
                .eq(ReportRecord::getTargetType, dto.getTargetType())
                .eq(ReportRecord::getTargetId, dto.getTargetId())
                .eq(ReportRecord::getStatus, "PENDING");
        if (reportMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("你已举报过该内容，请等待处理");
        }

        ReportRecord record = new ReportRecord();
        record.setReporterId(reporterId);
        record.setTargetType(dto.getTargetType());
        record.setTargetId(dto.getTargetId());
        record.setReason(dto.getReason());
        record.setDescription(dto.getDescription());
        record.setStatus("PENDING");
        reportMapper.insert(record);
    }

    @Override
    public List<ReportVO> listPendingReports() {
        return listPendingReports(1, 100).getRecords();
    }

    @Override
    public PageResult<ReportVO> listPendingReports(int page, int size) {
        Page<ReportRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ReportRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportRecord::getStatus, "PENDING")
                .orderByDesc(ReportRecord::getCreatedAt);
        Page<ReportRecord> result = reportMapper.selectPage(pageParam, wrapper);

        List<Long> reporterIds = result.getRecords().stream().map(ReportRecord::getReporterId).distinct().toList();
        Map<Long, String> nicknames = userService.getNicknameMap(reporterIds);

        List<ReportVO> items = result.getRecords().stream()
                .map(record -> toReportVO(record, nicknames))
                .collect(Collectors.toList());
        return PageResult.of(result.getCurrent(), result.getSize(), result.getTotal(), items);
    }

    private ReportVO toReportVO(ReportRecord record, Map<Long, String> nicknames) {
        ReportVO vo = new ReportVO();
        vo.setId(record.getId());
        vo.setReporterId(record.getReporterId());
        vo.setReporterNickname(nicknames.getOrDefault(record.getReporterId(), "用户" + record.getReporterId()));
        vo.setTargetType(record.getTargetType());
        vo.setTargetId(record.getTargetId());
        vo.setTargetSummary(buildTargetSummary(record.getTargetType(), record.getTargetId()));
        vo.setReason(record.getReason());
        vo.setDescription(record.getDescription());
        vo.setStatus(record.getStatus());
        vo.setHandlerId(record.getHandlerId());
        vo.setHandleResult(record.getHandleResult());
        vo.setCreatedAt(record.getCreatedAt());
        return vo;
    }

    @Override
    @Transactional
    public void handleReport(Long handlerId, Long reportId, HandleReportDTO dto) {
        if (!ACTIONS.contains(dto.getAction())) {
            throw new BusinessException("无效的处理动作");
        }

        ReportRecord record = reportMapper.selectById(reportId);
        if (record == null) {
            throw new BusinessException("举报记录不存在");
        }
        if (!"PENDING".equals(record.getStatus())) {
            throw new BusinessException("该举报已处理");
        }

        Long targetOwnerId = resolveTargetOwnerId(record.getTargetType(), record.getTargetId());

        switch (dto.getAction()) {
            case "HIDE_CONTENT" -> hideContent(record.getTargetType(), record.getTargetId());
            case "FREEZE_USER" -> freezeUser(targetOwnerId);
            case "DISMISS" -> { /* 仅标记驳回 */ }
            default -> throw new BusinessException("无效的处理动作");
        }

        record.setStatus("DISMISS".equals(dto.getAction()) ? "DISMISSED" : "RESOLVED");
        record.setHandlerId(handlerId);
        record.setHandleResult(dto.getHandleResult() != null ? dto.getHandleResult() : dto.getAction());
        reportMapper.updateById(record);
    }

    private void validateTargetType(String targetType) {
        if (!TARGET_TYPES.contains(targetType)) {
            throw new BusinessException("无效的目标类型");
        }
    }

    private Long validateTargetExists(String targetType, Long targetId, Long reporterId) {
        return switch (targetType) {
            case "CARD" -> {
                Card card = cardMapper.selectById(targetId);
                if (card == null) throw new BusinessException("卡片不存在");
                yield card.getUserId();
            }
            case "REPLY" -> {
                Reply reply = replyMapper.selectById(targetId);
                if (reply == null) throw new BusinessException("回复不存在");
                yield reply.getUserId();
            }
            case "MESSAGE" -> {
                PrivateMessage message = messageMapper.selectById(targetId);
                if (message == null) throw new BusinessException("消息不存在");
                CompanionRelation relation = relationMapper.selectById(message.getRelationId());
                if (relation == null) {
                    throw new BusinessException("消息所属关系不存在");
                }
                if (reporterId != null
                        && !reporterId.equals(relation.getInviterId())
                        && !reporterId.equals(relation.getInviteeId())) {
                    throw new BusinessException("无权举报该消息");
                }
                yield message.getSenderId();
            }
            case "USER" -> {
                User user = userMapper.selectById(targetId);
                if (user == null) throw new BusinessException("用户不存在");
                yield user.getId();
            }
            default -> throw new BusinessException("无效的目标类型");
        };
    }

    private Long resolveTargetOwnerId(String targetType, Long targetId) {
        return validateTargetExists(targetType, targetId, null);
    }

    private void hideContent(String targetType, Long targetId) {
        switch (targetType) {
            case "CARD" -> {
                Card card = cardMapper.selectById(targetId);
                if (card != null) {
                    card.setStatus("HIDDEN");
                    cardMapper.updateById(card);
                }
            }
            case "REPLY" -> {
                Reply reply = replyMapper.selectById(targetId);
                if (reply != null) {
                    reply.setIsHidden(1);
                    replyMapper.updateById(reply);
                }
            }
            case "MESSAGE" -> {
                PrivateMessage message = messageMapper.selectById(targetId);
                if (message != null) {
                    message.setIsRecalled(1);
                    messageMapper.updateById(message);
                }
            }
            case "USER" -> freezeUser(targetId);
            default -> throw new BusinessException("无法隐藏该类型内容");
        }
    }

    private void freezeUser(Long userId) {
        if (userId == null) {
            throw new BusinessException("无法确定被处理用户");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if ("ADMIN".equals(user.getRole())) {
            throw new BusinessException("不能冻结管理员账号");
        }
        user.setIsFrozen(1);
        userMapper.updateById(user);
    }

    private String buildTargetSummary(String targetType, Long targetId) {
        return switch (targetType) {
            case "CARD" -> {
                Card card = cardMapper.selectById(targetId);
                yield card != null ? truncate(card.getTitle() != null ? card.getTitle() : card.getEventDescription()) : "卡片#" + targetId;
            }
            case "REPLY" -> {
                Reply reply = replyMapper.selectById(targetId);
                yield reply != null ? truncate(reply.getExperienceSituation()) : "回复#" + targetId;
            }
            case "MESSAGE" -> {
                PrivateMessage message = messageMapper.selectById(targetId);
                yield message != null ? truncate(message.getContent()) : "消息#" + targetId;
            }
            case "USER" -> {
                User user = userMapper.selectById(targetId);
                yield user != null ? "用户：" + user.getNickname() : "用户#" + targetId;
            }
            default -> targetType + "#" + targetId;
        };
    }

    private String truncate(String text) {
        if (text == null) return "";
        return text.length() > 60 ? text.substring(0, 60) + "..." : text;
    }
}
