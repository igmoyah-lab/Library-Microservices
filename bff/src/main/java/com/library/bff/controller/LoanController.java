package com.library.bff.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.LoanRequest;
import com.library.bff.dto.LoanResponse;
import com.library.bff.services.LoanService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    /**
     * Crea un préstamo a través del BFF.
     *
     * @param request datos del préstamo
     * @return préstamo creado
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<LoanResponse> createLoan(
            @Valid @RequestBody LoanRequest request
    ) {
        return loanService.createLoan(request);
    }

    /**
     * Obtiene un préstamo mediante su ID.
     *
     * @param loanId identificador del préstamo
     * @return préstamo encontrado
     */
    @GetMapping("/{loanId}")
    public ApiResponse<LoanResponse> getLoanById(
            @PathVariable UUID loanId
    ) {
        return loanService.getLoanById(loanId);
    }

    /**
     * Obtiene los préstamos de un usuario.
     *
     * @param userId identificador del usuario
     * @return préstamos encontrados
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<List<LoanResponse>>
            getLoansByUserId(
                    @PathVariable UUID userId
            ) {
        return loanService.getLoansByUserId(userId);
    }

    /**
     * Obtiene el total de préstamos.
     *
     * @return total de préstamos
     */
    @GetMapping("/count")
    public ApiResponse<Long> countLoans() {
        return loanService.countLoans();
    }
}
