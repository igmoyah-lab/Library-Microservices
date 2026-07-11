package com.library.bff.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.GeneralReportResponse;
import com.library.bff.services.ReportService;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Obtiene el resumen general mediante el BFF.
     *
     * @return conteos generales del sistema
     */
    @GetMapping("/general-summary")
    public ApiResponse<GeneralReportResponse>
            getGeneralSummary() {

        return reportService.getGeneralSummary();
    }
}
