package com.library.returns.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.returns.entity.BookReturn;

public interface ReturnRepository extends JpaRepository<BookReturn, UUID> {

    Optional<BookReturn> findByLoanId(UUID loanId);

    boolean existsByLoanId(UUID loanId);
}