package com.library.returns.service;

import java.util.UUID;

import com.library.returns.dto.ApiResponse;
import com.library.returns.dto.ReturnRequest;
import com.library.returns.dto.ReturnResponse;

public interface ReturnService {

    ApiResponse<ReturnResponse> createReturn(ReturnRequest returnRequest);

    ApiResponse<ReturnResponse> getReturnByLoanId(UUID loanId);
}