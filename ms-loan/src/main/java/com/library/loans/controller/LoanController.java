package com.library.loans.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.loans.dto.ApiResponse;
import com.library.loans.dto.LoanRequest;
import com.library.loans.dto.LoanResponse;
import com.library.loans.service.LoanService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // CREAR UN PRÉSTAMO
    @PostMapping
    public ResponseEntity<ApiResponse<LoanResponse>> createLoan(@Valid @RequestBody LoanRequest loanRequest) {
        ApiResponse<LoanResponse> response =loanService.createLoan(loanRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // OBTENER LOS PRÉSTAMOS DE UN USUARIO
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getLoansByUserId(
            @PathVariable UUID userId
    ) {

        return ResponseEntity.ok(
                loanService.getLoansByUserId(userId)
        );
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countLoans() {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        loanService.countLoans(),
                        "Cantidad de préstamos obtenida con éxito"
                )
        );
}
}