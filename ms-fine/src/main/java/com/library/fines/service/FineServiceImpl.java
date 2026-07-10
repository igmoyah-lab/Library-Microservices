package com.library.fines.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.library.fines.dto.ApiResponse;
import com.library.fines.dto.FineRequest;
import com.library.fines.dto.FineResponse;
import com.library.fines.entity.Fine;
import com.library.fines.entity.FineStatus;
import com.library.fines.exception.DuplicateResourceException;
import com.library.fines.exception.ResourceNotFoundException;
import com.library.fines.repository.FineRepository;


@Service
public class FineServiceImpl implements FineService {

    private static final BigDecimal DAILY_FINE =
            BigDecimal.valueOf(1000);

    private final FineRepository fineRepository;

    public FineServiceImpl(FineRepository fineRepository) {
        this.fineRepository = fineRepository;
    }

    @Override
    public ApiResponse<FineResponse> createFine(
            FineRequest fineRequest
    ) {

        if (fineRepository.existsByLoanId(fineRequest.loanId())) {
            throw new DuplicateResourceException(
                    "El préstamo ya tiene una multa registrada"
            );
        }

        BigDecimal amount = DAILY_FINE.multiply(
                BigDecimal.valueOf(fineRequest.delayedDays())
        );

        Fine fine = new Fine();

        fine.setUserId(fineRequest.userId());
        fine.setLoanId(fineRequest.loanId());
        fine.setAmount(amount);
        fine.setFineDate(LocalDate.now());
        fine.setStatus(FineStatus.PENDING);

        Fine savedFine = fineRepository.save(fine);

        return new ApiResponse<>(
                true,
                mapToResponse(savedFine),
                "Multa registrada con éxito"
        );
    }

    @Override
    public ApiResponse<List<FineResponse>> getFinesByUserId(
            UUID userId
    ) {

        List<Fine> fines = fineRepository.findByUserId(userId);

        if (fines.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No se encontraron multas para el usuario: "
                            + userId
            );
        }

        List<FineResponse> responses = fines.stream()
                .map(this::mapToResponse)
                .toList();

        return new ApiResponse<>(
                true,
                responses,
                "Multas obtenidas con éxito"
        );
    }

    @Override
    public ApiResponse<FineResponse> payFine(UUID id) {

        Fine fine = fineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la multa con id: " + id
                ));

        if (fine.getStatus() == FineStatus.PAID) {
            throw new DuplicateResourceException(
                    "La multa ya se encuentra pagada"
            );
        }

        fine.setStatus(FineStatus.PAID);

        Fine updatedFine = fineRepository.save(fine);

        return new ApiResponse<>(
                true,
                mapToResponse(updatedFine),
                "Multa pagada con éxito"
        );
    }

    private FineResponse mapToResponse(Fine fine) {

        return new FineResponse(
                fine.getId(),
                fine.getUserId(),
                fine.getLoanId(),
                fine.getAmount(),
                fine.getFineDate(),
                fine.getStatus()
        );
    }

        @Override
        public long countFines() {
        return fineRepository.count();
}
}