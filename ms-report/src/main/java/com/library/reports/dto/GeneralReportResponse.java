package com.library.reports.dto;

public record GeneralReportResponse(
        long totalLoans,
        long totalFines,
        long pendingFines,
        long paidFines
) {
}