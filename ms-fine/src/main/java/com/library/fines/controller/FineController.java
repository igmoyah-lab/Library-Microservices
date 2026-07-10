package com.library.fines.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.fines.dto.ApiResponse;
import com.library.fines.dto.FineRequest;
import com.library.fines.dto.FineResponse;
import com.library.fines.service.FineService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/fines")
public class FineController {

    private final FineService fineService;

    public FineController(FineService fineService) {
        this.fineService = fineService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FineResponse>> createFine(
            @Valid @RequestBody FineRequest fineRequest
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(fineService.createFine(fineRequest));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<FineResponse>>>
            getFinesByUserId(
                    @PathVariable UUID userId
            ) {

        return ResponseEntity.ok(
                fineService.getFinesByUserId(userId)
        );
    }

    @PatchMapping("/{id}/pay")
    public ResponseEntity<ApiResponse<FineResponse>> payFine(
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                fineService.payFine(id)
        );
    }
       
    @GetMapping("/count")
        public ResponseEntity<ApiResponse<Long>> countFines() {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        fineService.countFines(),
                        "Cantidad de multas obtenida con éxito"
                )
        );
        }
}