
package com.library.reports.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.library.reports.dto.ApiResponse;
import com.library.reports.dto.GeneralReportResponse;
import com.library.reports.service.ReportService;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @Mock
    private GeneralReportResponse generalReportResponse;

    private ReportController reportController;

    @BeforeEach
    void setUp() {
        reportController = new ReportController(reportService);
    }

    @Test
    void getGeneralSummary_shouldReturnOkStatus() {
        ApiResponse<GeneralReportResponse> serviceResponse =
                new ApiResponse<>(
                        true,
                        generalReportResponse,
                        "Resumen general obtenido con éxito"
                );

        when(reportService.getGeneralSummary())
                .thenReturn(serviceResponse);

        ResponseEntity<
                ApiResponse<GeneralReportResponse>
        > response = reportController.getGeneralSummary();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(serviceResponse, response.getBody());

        verify(reportService).getGeneralSummary();
    }
}

