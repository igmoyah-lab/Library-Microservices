package com.library.loans.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.loans.entity.Loan;
import com.library.loans.entity.LoanStatus;

public interface LoanRepository
        extends JpaRepository<Loan, UUID> {

    List<Loan> findByUserId(UUID userId);

    boolean existsByBookIdAndStatus(
            UUID bookId,
            LoanStatus status
    );
}