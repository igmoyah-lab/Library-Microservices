package com.library.reports.service;

import com.library.reports.dto.ApiResponse;
import com.library.reports.dto.GeneralReportResponse;

public interface ReportService {

    ApiResponse<GeneralReportResponse> getGeneralSummary();
}