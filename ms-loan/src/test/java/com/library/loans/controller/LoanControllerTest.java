
package com.library.loans.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.library.loans.dto.ApiResponse;
import com.library.loans.dto.LoanRequest;
import com.library.loans.dto.LoanResponse;
import com.library.loans.service.LoanService;

@ExtendWith(MockitoExtension.class)
class LoanControllerTest {

    @Mock
    private LoanService loanService;

    @InjectMocks
    private LoanController loanController;

    private UUID loanId;
    private UUID userId;
    private LoanRequest loanRequest;
    private LoanResponse loanResponse;

    @BeforeEach
    void setUp() {
        loanId = UUID.randomUUID();
        userId = UUID.randomUUID();

        /*
         * Se utilizan mocks para no depender de los
         * constructores específicos de los DTO.
         */
        loanRequest = Mockito.mock(LoanRequest.class);
        loanResponse = Mockito.mock(LoanResponse.class);
    }

    @Test
    void createLoan_shouldReturnCreatedStatusAndLoanResponse() {
        ApiResponse<LoanResponse> serviceResponse =
                new ApiResponse<>(
                        true,
                        loanResponse,
                        "Préstamo creado con éxito"
                );

        when(loanService.createLoan(loanRequest))
                .thenReturn(serviceResponse);

        ResponseEntity<ApiResponse<LoanResponse>> response =
                loanController.createLoan(loanRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertSame(serviceResponse, response.getBody());

        verify(loanService, times(1))
                .createLoan(loanRequest);
    }

    @Test
    void getLoanById_shouldReturnOkAndLoanResponse() {
        ApiResponse<LoanResponse> serviceResponse =
                new ApiResponse<>(
                        true,
                        loanResponse,
                        "Préstamo obtenido con éxito"
                );

        when(loanService.getLoanById(loanId))
                .thenReturn(serviceResponse);

        ResponseEntity<ApiResponse<LoanResponse>> response =
                loanController.getLoanById(loanId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertSame(serviceResponse, response.getBody());

        verify(loanService, times(1))
                .getLoanById(loanId);
    }

    @Test
    void getLoansByUserId_shouldReturnOkAndListOfLoans() {
        LoanResponse secondLoanResponse =
                Mockito.mock(LoanResponse.class);

        List<LoanResponse> loans = List.of(
                loanResponse,
                secondLoanResponse
        );

        ApiResponse<List<LoanResponse>> serviceResponse =
                new ApiResponse<>(
                        true,
                        loans,
                        "Préstamos del usuario obtenidos con éxito"
                );

        when(loanService.getLoansByUserId(userId))
                .thenReturn(serviceResponse);

        ResponseEntity<ApiResponse<List<LoanResponse>>> response =
                loanController.getLoansByUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertSame(serviceResponse, response.getBody());
        assertEquals(2, response.getBody().data().size());

        verify(loanService, times(1))
                .getLoansByUserId(userId);
    }

    @Test
    void markLoanAsReturned_shouldReturnOkAndUpdatedLoan() {
        ApiResponse<LoanResponse> serviceResponse =
                new ApiResponse<>(
                        true,
                        loanResponse,
                        "Préstamo devuelto con éxito"
                );

        when(loanService.markLoanAsReturned(loanId))
                .thenReturn(serviceResponse);

        ResponseEntity<ApiResponse<LoanResponse>> response =
                loanController.markLoanAsReturned(loanId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertSame(serviceResponse, response.getBody());

        verify(loanService, times(1))
                .markLoanAsReturned(loanId);
    }

    @Test
    void countLoans_shouldReturnOkAndTotalCount() {
        long totalLoans = 12L;

        when(loanService.countLoans())
                .thenReturn(totalLoans);

        ResponseEntity<ApiResponse<Long>> response =
                loanController.countLoans();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(true, response.getBody().success());
        assertEquals(totalLoans, response.getBody().data());
        assertEquals(
                "Cantidad de préstamos obtenida con éxito",
                response.getBody().message()
        );

        verify(loanService, times(1))
                .countLoans();
    }
}
