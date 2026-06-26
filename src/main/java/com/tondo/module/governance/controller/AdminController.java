package com.tondo.module.governance.controller;

import com.tondo.common.annotation.OperationLog;
import com.tondo.common.response.PageResult;
import com.tondo.common.response.Result;
import com.tondo.module.governance.entity.dto.AdminUpdateUserRoleDTO;
import com.tondo.module.governance.entity.dto.AdminUpdateUserStatusDTO;
import com.tondo.module.governance.entity.dto.HandleReportDTO;
import com.tondo.module.governance.entity.vo.OperationLogVO;
import com.tondo.module.governance.entity.vo.ReportVO;
import com.tondo.module.governance.service.OperationLogService;
import com.tondo.module.governance.service.ReportService;
import com.tondo.module.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ReportService reportService;
    private final OperationLogService operationLogService;
    private final UserService userService;

    @GetMapping("/reports")
    public Result<PageResult<ReportVO>> listPendingReports(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.success(reportService.listPendingReports(page, size));
    }

    @PutMapping("/reports/{reportId}")
    @OperationLog(action = "HANDLE_REPORT", targetType = "REPORT", targetIdSpel = "#reportId")
    public Result<?> handleReport(@RequestAttribute("userId") Long handlerId,
                                  @PathVariable Long reportId,
                                  @Valid @RequestBody HandleReportDTO dto) {
        reportService.handleReport(handlerId, reportId, dto);
        return Result.success();
    }

    @GetMapping("/operation-logs")
    public Result<PageResult<OperationLogVO>> listOperationLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.success(operationLogService.listLogs(page, size));
    }

    @PutMapping("/users/{userId}/status")
    @OperationLog(action = "UPDATE_USER_STATUS", targetType = "USER", targetIdSpel = "#userId")
    public Result<?> updateUserStatus(@PathVariable Long userId,
                                      @RequestBody AdminUpdateUserStatusDTO dto) {
        userService.adminUpdateUserStatus(userId, dto.isFrozen());
        return Result.success();
    }

    @PutMapping("/users/{userId}/role")
    @OperationLog(action = "UPDATE_USER_ROLE", targetType = "USER", targetIdSpel = "#userId")
    public Result<?> updateUserRole(@PathVariable Long userId,
                                    @Valid @RequestBody AdminUpdateUserRoleDTO dto) {
        userService.adminUpdateUserRole(userId, dto.getRole());
        return Result.success();
    }
}
