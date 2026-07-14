package com.library.bff.services;

import org.springframework.stereotype.Service;

import com.library.bff.client.ReportClient;
import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.GeneralReportResponse;

@Service
public class ReportService {

    private final ReportClient reportClient;

    public ReportService(ReportClient reportClient) {
        this.reportClient = reportClient;
    }

    /**
     * Obtiene el resumen general del sistema.
     *
     * @return reporte con préstamos y multas
     */
    public ApiResponse<GeneralReportResponse>
            getGeneralSummary() {

        return reportClient.getGeneralSummary();
    }
}
