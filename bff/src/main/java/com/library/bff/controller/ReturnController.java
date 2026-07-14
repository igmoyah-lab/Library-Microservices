package com.library.bff.controller;

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
import com.library.bff.dto.ReturnRequest;
import com.library.bff.dto.ReturnResponse;
import com.library.bff.services.ReturnService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/returns")
public class ReturnController {

    private final ReturnService returnService;

    public ReturnController(
            ReturnService returnService
    ) {
        this.returnService = returnService;
    }

    /**
     * Registra una devolución mediante el BFF.
     *
     * @param request identificador del préstamo
     * @return devolución registrada
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReturnResponse> createReturn(
            @Valid @RequestBody ReturnRequest request
    ) {
        return returnService.createReturn(request);
    }

    /**
     * Consulta una devolución mediante el ID del préstamo.
     *
     * @param loanId identificador del préstamo
     * @return devolución encontrada
     */
    @GetMapping("/loan/{loanId}")
    public ApiResponse<ReturnResponse> getReturnByLoanId(
            @PathVariable UUID loanId
    ) {
        return returnService.getReturnByLoanId(loanId);
    }
}
