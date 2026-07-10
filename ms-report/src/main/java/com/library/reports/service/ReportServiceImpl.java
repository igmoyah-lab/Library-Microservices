package com.library.reports.service;

import org.springframework.stereotype.Service;

import com.library.reports.client.FineClient;
import com.library.reports.client.LoanClient;
import com.library.reports.dto.ApiResponse;
import com.library.reports.dto.GeneralReportResponse;


@Service
public class ReportServiceImpl implements ReportService {

    private final LoanClient loanClient;
    private final FineClient fineClient;

    public ReportServiceImpl(
            LoanClient loanClient,
            FineClient fineClient
    ) {
        this.loanClient = loanClient;
        this.fineClient = fineClient;
    }

    @Override
    public ApiResponse<GeneralReportResponse> getGeneralSummary() {

        long totalLoans = loanClient.getTotalLoans();
        long totalFines = fineClient.getTotalFines();

        GeneralReportResponse report =
                new GeneralReportResponse(
                        totalLoans,
                        totalFines
                );

        return new ApiResponse<>(
                true,
                report,
                "Reporte general obtenido con éxito"
        );
    }
}
