package com.library.bff.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.library.bff.client.FineClient;
import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.FineResponse;

@Service
public class FineService {

    private final FineClient fineClient;

    public FineService(FineClient fineClient) {
        this.fineClient = fineClient;
    }

    /**
     * Obtiene una multa mediante su ID.
     *
     * @param fineId identificador de la multa
     * @return multa encontrada
     */
    public ApiResponse<FineResponse> getFineById(
            UUID fineId
    ) {
        return fineClient.getFineById(fineId);
    }

    /**
     * Obtiene las multas de un usuario.
     *
     * @param userId identificador del usuario
     * @return multas encontradas
     */
    public ApiResponse<List<FineResponse>> getFinesByUserId(
            UUID userId
    ) {
        return fineClient.getFinesByUserId(userId);
    }

    /**
     * Paga una multa pendiente.
     *
     * @param fineId identificador de la multa
     * @return multa pagada
     */
    public ApiResponse<FineResponse> payFine(
            UUID fineId
    ) {
        return fineClient.payFine(fineId);
    }

    /**
     * Obtiene el total de multas.
     *
     * @return total de multas
     */
    public ApiResponse<Long> countFines() {
        return fineClient.countFines();
    }

    /**
     * Obtiene el total de multas pendientes.
     *
     * @return total de multas pendientes
     */
    public ApiResponse<Long> countPendingFines() {
        return fineClient.countPendingFines();
    }

    /**
     * Obtiene el total de multas pagadas.
     *
     * @return total de multas pagadas
     */
    public ApiResponse<Long> countPaidFines() {
        return fineClient.countPaidFines();
    }
}
