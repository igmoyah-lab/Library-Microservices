package com.library.reports.dto;

public record GeneralReportResponse(
        long totalLoans,
        long totalFines
) {
}