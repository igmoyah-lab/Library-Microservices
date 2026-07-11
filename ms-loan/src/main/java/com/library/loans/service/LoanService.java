package com.library.loans.service;

import java.util.List;
import java.util.UUID;

import com.library.loans.dto.ApiResponse;
import com.library.loans.dto.LoanRequest;
import com.library.loans.dto.LoanResponse;

public interface LoanService {

    /**
     * Crea un préstamo después de validar al usuario,
     * el libro y su disponibilidad.
     *
     * @param loanRequest datos necesarios para el préstamo
     * @return respuesta con el préstamo creado
     */
    ApiResponse<LoanResponse> createLoan(
            LoanRequest loanRequest
    );

    /**
     * Obtiene los préstamos pertenecientes a un usuario.
     *
     * @param userId identificador del usuario
     * @return respuesta con los préstamos encontrados
     */
    ApiResponse<List<LoanResponse>> getLoansByUserId(
            UUID userId
    );

    /**
     * Obtiene la cantidad total de préstamos.
     *
     * @return cantidad total de préstamos
     */
    long countLoans();
}