package com.library.fines.service;

import java.util.List;
import java.util.UUID;

import com.library.fines.dto.ApiResponse;
import com.library.fines.dto.FineRequest;
import com.library.fines.dto.FineResponse;

public interface FineService {

    ApiResponse<FineResponse> createFine(FineRequest fineRequest);

    ApiResponse<List<FineResponse>> getFinesByUserId(UUID userId);

    ApiResponse<FineResponse> payFine(UUID id);

    long countFines();
}