package com.tondo.module.governance.service;

import com.tondo.common.response.PageResult;
import com.tondo.module.governance.entity.dto.CreateReportDTO;
import com.tondo.module.governance.entity.dto.HandleReportDTO;
import com.tondo.module.governance.entity.vo.ReportVO;

import java.util.List;

public interface ReportService {
    void createReport(Long reporterId, CreateReportDTO dto);

    List<ReportVO> listPendingReports();

    PageResult<ReportVO> listPendingReports(int page, int size);

    void handleReport(Long handlerId, Long reportId, HandleReportDTO dto);
}
