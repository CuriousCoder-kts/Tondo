package com.tondo.module.notification.controller;

import com.tondo.common.response.PageResult;
import com.tondo.common.response.Result;
import com.tondo.module.notification.entity.vo.NotificationItemVO;
import com.tondo.module.notification.entity.vo.NotificationSummaryVO;
import com.tondo.module.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/summary")
    public Result<NotificationSummaryVO> summary(@RequestAttribute("userId") Long userId) {
        return Result.success(notificationService.getSummary(userId));
    }

    @GetMapping
    public Result<PageResult<NotificationItemVO>> list(@RequestAttribute("userId") Long userId,
                                                       @RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "20") int size) {
        return Result.success(notificationService.listInbox(userId, page, size));
    }

    @PutMapping("/{id}/read")
    public Result<?> markRead(@RequestAttribute("userId") Long userId, @PathVariable Long id) {
        notificationService.markRead(userId, id);
        return Result.success();
    }

    @PutMapping("/read-all")
    public Result<?> markAllRead(@RequestAttribute("userId") Long userId) {
        notificationService.markAllRead(userId);
        return Result.success();
    }
}
