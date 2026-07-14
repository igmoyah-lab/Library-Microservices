package com.library.reports.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.reports.dto.ApiResponse;
import com.library.reports.dto.GeneralReportResponse;
import com.library.reports.service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(
            ReportService reportService
    ) {
        this.reportService = reportService;
    }

    /**
     * Obtiene un resumen general del sistema.
     *
     * @return conteos de préstamos y multas
     */
    @GetMapping("/general-summary")
    public ResponseEntity<
            ApiResponse<GeneralReportResponse>
    > getGeneralSummary() {

        return ResponseEntity.ok(
                reportService.getGeneralSummary()
        );
    }
}