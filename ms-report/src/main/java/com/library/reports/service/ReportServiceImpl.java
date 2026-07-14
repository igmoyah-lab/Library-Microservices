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

    /**
     * Genera un resumen general consultando los conteos
     * disponibles en ms-loan y ms-fine.
     *
     * @return respuesta con el reporte general
     */
    @Override
    public ApiResponse<GeneralReportResponse>
            getGeneralSummary() {

        long totalLoans =
                loanClient.getTotalLoans();

        long totalFines =
                fineClient.getTotalFines();

        long pendingFines =
                fineClient.getPendingFines();

        long paidFines =
                fineClient.getPaidFines();

        GeneralReportResponse report =
                new GeneralReportResponse(
                        totalLoans,
                        totalFines,
                        pendingFines,
                        paidFines
                );

        return new ApiResponse<>(
                true,
                report,
                "Reporte general obtenido con éxito"
        );
    }
}