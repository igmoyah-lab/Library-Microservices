package com.library.returns.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.returns.dto.ApiResponse;
import com.library.returns.dto.ReturnRequest;
import com.library.returns.dto.ReturnResponse;
import com.library.returns.service.ReturnService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/returns")
public class ReturnController {

    private final ReturnService returnService;

    public ReturnController(ReturnService returnService) {
        this.returnService = returnService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReturnResponse>> createReturn(
            @Valid @RequestBody ReturnRequest returnRequest
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(returnService.createReturn(returnRequest));
    }

    @GetMapping("/loan/{loanId}")
    public ResponseEntity<ApiResponse<ReturnResponse>> getReturnByLoanId(
            @PathVariable UUID loanId
    ) {

        return ResponseEntity.ok(
                returnService.getReturnByLoanId(loanId)
        );
    }
}