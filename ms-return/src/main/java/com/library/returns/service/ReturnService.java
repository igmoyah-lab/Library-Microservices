package com.library.returns.service;

import java.util.UUID;

import com.library.returns.dto.ApiResponse;
import com.library.returns.dto.ReturnRequest;
import com.library.returns.dto.ReturnResponse;

public interface ReturnService {

    /**
     * Registra la devolución de un préstamo y coordina
     * la actualización del préstamo, libro y multa.
     *
     * @param returnRequest datos de la devolución
     * @return devolución registrada
     */
    ApiResponse<ReturnResponse> createReturn(
            ReturnRequest returnRequest
    );

    /**
     * Obtiene una devolución mediante el identificador
     * del préstamo.
     *
     * @param loanId identificador del préstamo
     * @return devolución encontrada
     */
    ApiResponse<ReturnResponse> getReturnByLoanId(
            UUID loanId
    );
}