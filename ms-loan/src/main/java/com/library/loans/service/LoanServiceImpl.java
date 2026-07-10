package com.library.loans.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.library.loans.dto.ApiResponse;
import com.library.loans.dto.LoanRequest;
import com.library.loans.dto.LoanResponse;
import com.library.loans.entity.Loan;
import com.library.loans.entity.LoanStatus;
import com.library.loans.exception.ResourceNotFoundException;
import com.library.loans.repository.LoanRepository;

@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;

    public LoanServiceImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    // CREAR UN PRÉSTAMO
    @Override
    public ApiResponse<LoanResponse> createLoan(LoanRequest loanRequest) {

        Loan loan = new Loan();

        loan.setUserId(loanRequest.userId());
        loan.setBookId(loanRequest.bookId());

        LocalDate currentDate = LocalDate.now();

        loan.setLoanDate(currentDate);
        loan.setDueDate(currentDate.plusDays(7));
        loan.setStatus(LoanStatus.ACTIVE);

        Loan savedLoan = loanRepository.save(loan);

        return new ApiResponse<>(
                true,
                mapToResponse(savedLoan),
                "Préstamo creado con éxito"
        );
    }

    // OBTENER PRÉSTAMOS DE UN USUARIO
    @Override
    public ApiResponse<List<LoanResponse>> getLoansByUserId(UUID userId) {

        List<Loan> loans = loanRepository.findByUserId(userId);

        if (loans.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No se encontraron préstamos para el usuario con id: " + userId
            );
        }

        List<LoanResponse> loanResponses = loans.stream()
                .map(this::mapToResponse)
                .toList();

        return new ApiResponse<>(
                true,
                loanResponses,
                "Préstamos obtenidos con éxito"
        );
    }

    // CONVERTIR ENTIDAD EN DTO DE RESPUESTA
    private LoanResponse mapToResponse(Loan loan) {

        return new LoanResponse(
                loan.getId(),
                loan.getUserId(),
                loan.getBookId(),
                loan.getLoanDate(),
                loan.getDueDate(),
                loan.getStatus()
        );
    }
}