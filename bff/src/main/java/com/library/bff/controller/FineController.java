package com.library.bff.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.FineResponse;
import com.library.bff.services.FineService;

@RestController
@RequestMapping("/fines")
public class FineController {

    private final FineService fineService;

    public FineController(FineService fineService) {
        this.fineService = fineService;
    }

    /**
     * Obtiene una multa mediante su ID.
     *
     * @param fineId identificador de la multa
     * @return multa encontrada
     */
    @GetMapping("/{fineId}")
    public ApiResponse<FineResponse> getFineById(
            @PathVariable UUID fineId
    ) {
        return fineService.getFineById(fineId);
    }

    /**
     * Obtiene las multas de un usuario.
     *
     * @param userId identificador del usuario
     * @return multas encontradas
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<List<FineResponse>> getFinesByUserId(
            @PathVariable UUID userId
    ) {
        return fineService.getFinesByUserId(userId);
    }

    /**
     * Marca una multa como pagada.
     *
     * @param fineId identificador de la multa
     * @return multa actualizada
     */
    @PatchMapping("/{fineId}/pay")
    public ApiResponse<FineResponse> payFine(
            @PathVariable UUID fineId
    ) {
        return fineService.payFine(fineId);
    }

    /**
     * Obtiene el total de multas registradas.
     *
     * @return total de multas
     */
    @GetMapping("/count")
    public ApiResponse<Long> countFines() {
        return fineService.countFines();
    }

    /**
     * Obtiene el total de multas pendientes.
     *
     * @return total de multas pendientes
     */
    @GetMapping("/count/pending")
    public ApiResponse<Long> countPendingFines() {
        return fineService.countPendingFines();
    }

    /**
     * Obtiene el total de multas pagadas.
     *
     * @return total de multas pagadas
     */
    @GetMapping("/count/paid")
    public ApiResponse<Long> countPaidFines() {
        return fineService.countPaidFines();
    }
}
