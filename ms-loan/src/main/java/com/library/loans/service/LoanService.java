package com.library.loans.service;

import java.util.List;
import java.util.UUID;

import com.library.loans.dto.ApiResponse;
import com.library.loans.dto.LoanRequest;
import com.library.loans.dto.LoanResponse;

public interface LoanService {

    ApiResponse<LoanResponse> createLoan(LoanRequest loanRequest);

    ApiResponse<List<LoanResponse>> getLoansByUserId(UUID userId);
}