package com.library.fines.controller;

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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.library.fines.dto.ApiResponse;
import com.library.fines.dto.FineRequest;
import com.library.fines.dto.FineResponse;
import com.library.fines.service.FineService;

@ExtendWith(MockitoExtension.class)
class FineControllerTest {

    @Mock
    private FineService fineService;

    @InjectMocks
    private FineController fineController;

    private UUID fineId;
    private UUID userId;
    private FineRequest fineRequest;
    private FineResponse fineResponse;

    @BeforeEach
    void setUp() {
        fineId = UUID.randomUUID();
        userId = UUID.randomUUID();

        /*
         * Se utilizan mocks para no depender de los constructores
         * específicos de los DTO.
         */
        fineRequest = org.mockito.Mockito.mock(FineRequest.class);
        fineResponse = org.mockito.Mockito.mock(FineResponse.class);
    }

    @Test
    void createFine_shouldReturnCreatedStatusAndFineResponse() {
        ApiResponse<FineResponse> serviceResponse = new ApiResponse<>(
                true,
                fineResponse,
                "Multa creada con éxito"
        );

        when(fineService.createFine(fineRequest))
                .thenReturn(serviceResponse);

        ResponseEntity<ApiResponse<FineResponse>> response =
                fineController.createFine(fineRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertSame(serviceResponse, response.getBody());

        verify(fineService, times(1))
                .createFine(fineRequest);
    }

    @Test
    void getFineById_shouldReturnOkAndFineResponse() {
        ApiResponse<FineResponse> serviceResponse = new ApiResponse<>(
                true,
                fineResponse,
                "Multa encontrada con éxito"
        );

        when(fineService.getFineById(fineId))
                .thenReturn(serviceResponse);

        ResponseEntity<ApiResponse<FineResponse>> response =
                fineController.getFineById(fineId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertSame(serviceResponse, response.getBody());

        verify(fineService, times(1))
                .getFineById(fineId);
    }

    @Test
    void getFinesByUserId_shouldReturnOkAndListOfFines() {
        FineResponse secondFineResponse =
                org.mockito.Mockito.mock(FineResponse.class);

        List<FineResponse> fines = List.of(
                fineResponse,
                secondFineResponse
        );

        ApiResponse<List<FineResponse>> serviceResponse =
                new ApiResponse<>(
                        true,
                        fines,
                        "Multas del usuario obtenidas con éxito"
                );

        when(fineService.getFinesByUserId(userId))
                .thenReturn(serviceResponse);

        ResponseEntity<ApiResponse<List<FineResponse>>> response =
                fineController.getFinesByUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertSame(serviceResponse, response.getBody());
        assertEquals(2, response.getBody().data().size());

        verify(fineService, times(1))
                .getFinesByUserId(userId);
    }

    @Test
    void payFine_shouldReturnOkAndPaidFineResponse() {
        ApiResponse<FineResponse> serviceResponse = new ApiResponse<>(
                true,
                fineResponse,
                "Multa pagada con éxito"
        );

        when(fineService.payFine(fineId))
                .thenReturn(serviceResponse);

        ResponseEntity<ApiResponse<FineResponse>> response =
                fineController.payFine(fineId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertSame(serviceResponse, response.getBody());

        verify(fineService, times(1))
                .payFine(fineId);
    }

    @Test
    void countFines_shouldReturnOkAndTotalCount() {
        long totalFines = 10L;

        when(fineService.countFines())
                .thenReturn(totalFines);

        ResponseEntity<ApiResponse<Long>> response =
                fineController.countFines();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().success());
        assertEquals(totalFines, response.getBody().data());
        assertEquals(
                "Cantidad total de multas obtenida con éxito",
                response.getBody().message()
        );

        verify(fineService, times(1))
                .countFines();
    }

    @Test
    void countPendingFines_shouldReturnOkAndPendingCount() {
        long pendingFines = 6L;

        when(fineService.countPendingFines())
                .thenReturn(pendingFines);

        ResponseEntity<ApiResponse<Long>> response =
                fineController.countPendingFines();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().success());
        assertEquals(pendingFines, response.getBody().data());
        assertEquals(
                "Cantidad de multas pendientes obtenida con éxito",
                response.getBody().message()
        );

        verify(fineService, times(1))
                .countPendingFines();
    }

    @Test
    void countPaidFines_shouldReturnOkAndPaidCount() {
        long paidFines = 4L;

        when(fineService.countPaidFines())
                .thenReturn(paidFines);

        ResponseEntity<ApiResponse<Long>> response =
                fineController.countPaidFines();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().success());
        assertEquals(paidFines, response.getBody().data());
        assertEquals(
                "Cantidad de multas pagadas obtenida con éxito",
                response.getBody().message()
        );

        verify(fineService, times(1))
                .countPaidFines();
    }
}