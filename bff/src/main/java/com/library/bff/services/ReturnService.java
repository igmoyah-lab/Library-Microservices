package com.library.bff.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.library.bff.client.ReturnClient;
import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.ReturnRequest;
import com.library.bff.dto.ReturnResponse;

@Service
public class ReturnService {

    private final ReturnClient returnClient;

    public ReturnService(ReturnClient returnClient) {
        this.returnClient = returnClient;
    }

    /**
     * Registra la devolución de un préstamo.
     *
     * @param request identificador del préstamo
     * @return devolución registrada
     */
    public ApiResponse<ReturnResponse> createReturn(
            ReturnRequest request
    ) {
        return returnClient.createReturn(request);
    }

    /**
     * Obtiene una devolución mediante el ID del préstamo.
     *
     * @param loanId identificador del préstamo
     * @return devolución encontrada
     */
    public ApiResponse<ReturnResponse> getReturnByLoanId(
            UUID loanId
    ) {
        return returnClient.getReturnByLoanId(loanId);
    }
}