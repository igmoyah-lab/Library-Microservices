package com.library.reports.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.library.reports.client.FineClient;
import com.library.reports.client.LoanClient;
import com.library.reports.dto.ApiResponse;
import com.library.reports.dto.GeneralReportResponse;
import com.library.reports.exception.ExternalServiceException;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private LoanClient loanClient;

    @Mock
    private FineClient fineClient;

    private ReportServiceImpl createService() {
        return new ReportServiceImpl(
                loanClient,
                fineClient
        );
    }

    @Test
    void getGeneralSummary_shouldReturnCompleteReport() {
        ReportServiceImpl reportService =
                createService();

        when(loanClient.getTotalLoans())
                .thenReturn(10L);

        when(fineClient.getTotalFines())
                .thenReturn(5L);

        when(fineClient.getPendingFines())
                .thenReturn(3L);

        when(fineClient.getPaidFines())
                .thenReturn(2L);

        ApiResponse<GeneralReportResponse> result =
                reportService.getGeneralSummary();

        assertTrue(result.success());
        assertNotNull(result.data());

        assertEquals(
                10L,
                result.data().totalLoans()
        );

        assertEquals(
                5L,
                result.data().totalFines()
        );

        assertEquals(
                3L,
                result.data().pendingFines()
        );

        assertEquals(
                2L,
                result.data().paidFines()
        );

        assertEquals(
                "Reporte general obtenido con éxito",
                result.message()
        );

        verify(loanClient).getTotalLoans();
        verify(fineClient).getTotalFines();
        verify(fineClient).getPendingFines();
        verify(fineClient).getPaidFines();
    }

    @Test
    void getGeneralSummary_shouldPropagateLoanServiceFailure() {
        ReportServiceImpl reportService =
                createService();

        when(loanClient.getTotalLoans())
                .thenThrow(
                        new ExternalServiceException(
                                "No fue posible obtener préstamos"
                        )
                );

        ExternalServiceException exception =
                assertThrows(
                        ExternalServiceException.class,
                        reportService::getGeneralSummary
                );

        assertEquals(
                "No fue posible obtener préstamos",
                exception.getMessage()
        );

        verify(fineClient, never())
                .getTotalFines();

        verify(fineClient, never())
                .getPendingFines();

        verify(fineClient, never())
                .getPaidFines();
    }

    @Test
    void getGeneralSummary_shouldPropagateFineServiceFailure() {
        ReportServiceImpl reportService =
                createService();

        when(loanClient.getTotalLoans())
                .thenReturn(10L);

        when(fineClient.getTotalFines())
                .thenThrow(
                        new ExternalServiceException(
                                "No fue posible obtener multas"
                        )
                );

        ExternalServiceException exception =
                assertThrows(
                        ExternalServiceException.class,
                        reportService::getGeneralSummary
                );

        assertEquals(
                "No fue posible obtener multas",
                exception.getMessage()
        );

        verify(loanClient).getTotalLoans();
        verify(fineClient).getTotalFines();

        verify(fineClient, never())
                .getPendingFines();

        verify(fineClient, never())
                .getPaidFines();
    }
}
