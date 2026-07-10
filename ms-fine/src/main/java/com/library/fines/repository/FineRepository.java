package com.library.fines.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.fines.entity.Fine;

public interface FineRepository extends JpaRepository<Fine, UUID> {

    List<Fine> findByUserId(UUID userId);

    boolean existsByLoanId(UUID loanId);
}
