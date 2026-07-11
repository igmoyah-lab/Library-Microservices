package com.library.bff.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.GeneralReportResponse;

@Component
public class ReportClient {

    private final RestClient restClient;

    public ReportClient(
            @Value("${report.service.base-url}")
            String reportBaseUrl
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(reportBaseUrl)
                .build();
    }

    /**
     * Obtiene el resumen general desde ms-report.
     *
     * @return reporte con los conteos generales
     */
    public ApiResponse<GeneralReportResponse>
            getGeneralSummary() {

        return restClient.get()
                .uri("/api/reports/general-summary")
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                ApiResponse<GeneralReportResponse>
                        >() {
                        }
                );
    }
}
