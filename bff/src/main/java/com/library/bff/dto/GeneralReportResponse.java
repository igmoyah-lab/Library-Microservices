package com.library.bff.dto;

public record GeneralReportResponse(
        long totalLoans,
        long totalFines,
        long pendingFines,
        long paidFines
) {
}