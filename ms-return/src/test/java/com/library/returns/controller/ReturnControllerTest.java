package com.library.returns.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.library.returns.dto.ApiResponse;
import com.library.returns.dto.ReturnRequest;
import com.library.returns.dto.ReturnResponse;
import com.library.returns.service.ReturnService;

@ExtendWith(MockitoExtension.class)
class ReturnControllerTest {

    @Mock
    private ReturnService returnService;

    @Mock
    private ReturnRequest returnRequest;

    @Mock
    private ReturnResponse returnResponse;

    private ReturnController returnController;

    @BeforeEach
    void setUp() {
        returnController = new ReturnController(returnService);
    }

    @Test
    void createReturn_shouldReturnCreatedStatus() {
        ApiResponse<ReturnResponse> serviceResponse =
                new ApiResponse<>(
                        true,
                        returnResponse,
                        "Devolución registrada con éxito"
                );

        when(returnService.createReturn(returnRequest))
                .thenReturn(serviceResponse);

        ResponseEntity<ApiResponse<ReturnResponse>> response =
                returnController.createReturn(returnRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(serviceResponse, response.getBody());

        verify(returnService).createReturn(returnRequest);
    }

    @Test
    void getReturnByLoanId_shouldReturnOkStatus() {
        UUID loanId = UUID.randomUUID();

        ApiResponse<ReturnResponse> serviceResponse =
                new ApiResponse<>(
                        true,
                        returnResponse,
                        "Devolución obtenida con éxito"
                );

        when(returnService.getReturnByLoanId(loanId))
                .thenReturn(serviceResponse);

        ResponseEntity<ApiResponse<ReturnResponse>> response =
                returnController.getReturnByLoanId(loanId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(serviceResponse, response.getBody());

        verify(returnService).getReturnByLoanId(loanId);
    }
}