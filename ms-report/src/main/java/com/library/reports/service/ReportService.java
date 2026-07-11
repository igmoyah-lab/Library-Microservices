package com.library.reports.service;

import com.library.reports.dto.ApiResponse;
import com.library.reports.dto.GeneralReportResponse;

public interface ReportService {

    /**
     * Genera un resumen general consultando
     * los microservicios de préstamos y multas.
     *
     * @return respuesta con el reporte general
     */
    ApiResponse<GeneralReportResponse> getGeneralSummary();
}