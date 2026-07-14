package com.library.bff.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.library.bff.client.LoanClient;
import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.LoanRequest;
import com.library.bff.dto.LoanResponse;

@Service
public class LoanService {

    private final LoanClient loanClient;

    public LoanService(LoanClient loanClient) {
        this.loanClient = loanClient;
    }

    /**
     * Crea un préstamo mediante ms-loan.
     *
     * @param request datos del préstamo
     * @return préstamo creado
     */
    public ApiResponse<LoanResponse> createLoan(
            LoanRequest request
    ) {
        return loanClient.createLoan(request);
    }

    /**
     * Obtiene un préstamo mediante su ID.
     *
     * @param loanId identificador del préstamo
     * @return préstamo encontrado
     */
    public ApiResponse<LoanResponse> getLoanById(
            UUID loanId
    ) {
        return loanClient.getLoanById(loanId);
    }

    /**
     * Obtiene los préstamos de un usuario.
     *
     * @param userId identificador del usuario
     * @return préstamos encontrados
     */
    public ApiResponse<List<LoanResponse>>
            getLoansByUserId(UUID userId) {
        return loanClient.getLoansByUserId(userId);
    }

    /**
     * Obtiene el total de préstamos registrados.
     *
     * @return total de préstamos
     */
    public ApiResponse<Long> countLoans() {
        return loanClient.countLoans();
    }
}
