package com.library.fines.service;

import java.util.List;
import java.util.UUID;

import com.library.fines.dto.ApiResponse;
import com.library.fines.dto.FineRequest;
import com.library.fines.dto.FineResponse;

public interface FineService {
    /**
     * Crea una multa calculando su monto según
     * los días de atraso.
     *
     * @param fineRequest datos necesarios para crear la multa
     * @return respuesta con la multa creada
     */
    ApiResponse<FineResponse> createFine(
            FineRequest fineRequest
    );

    /**
     * Obtiene una multa mediante su identificador.
     *
     * @param id identificador de la multa
     * @return respuesta con la multa encontrada
     */
    ApiResponse<FineResponse> getFineById(UUID id);

    /**
     * Obtiene todas las multas pertenecientes
     * a un usuario.
     *
     * @param userId identificador del usuario
     * @return respuesta con las multas encontradas
     */
    ApiResponse<List<FineResponse>> getFinesByUserId(
            UUID userId
    );

    /**
     * Marca una multa pendiente como pagada.
     *
     * @param id identificador de la multa
     * @return respuesta con la multa actualizada
     */
    ApiResponse<FineResponse> payFine(UUID id);

    /**
     * Cuenta todas las multas registradas.
     *
     * @return cantidad total de multas
     */
    long countFines();

    /**
     * Cuenta las multas pendientes de pago.
     *
     * @return cantidad de multas pendientes
     */
    long countPendingFines();

    /**
     * Cuenta las multas pagadas.
     *
     * @return cantidad de multas pagadas
     */
    long countPaidFines();
}