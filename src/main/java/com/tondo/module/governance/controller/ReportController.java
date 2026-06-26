package com.tondo.module.governance.controller;

import com.tondo.common.response.Result;
import com.tondo.module.governance.entity.dto.CreateReportDTO;
import com.tondo.module.governance.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public Result<?> createReport(@RequestAttribute("userId") Long userId,
                                  @Valid @RequestBody CreateReportDTO dto) {
        reportService.createReport(userId, dto);
        return Result.success();
    }
}
