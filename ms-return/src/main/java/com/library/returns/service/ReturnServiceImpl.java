package com.library.returns.service;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.library.returns.dto.ApiResponse;
import com.library.returns.dto.ReturnRequest;
import com.library.returns.dto.ReturnResponse;
import com.library.returns.entity.BookReturn;
import com.library.returns.exception.DuplicateResourceException;
import com.library.returns.exception.ResourceNotFoundException;
import com.library.returns.repository.ReturnRepository;


@Service
public class ReturnServiceImpl implements ReturnService {

    private final ReturnRepository returnRepository;

    public ReturnServiceImpl(ReturnRepository returnRepository) {
        this.returnRepository = returnRepository;
    }

    @Override
    public ApiResponse<ReturnResponse> createReturn(ReturnRequest returnRequest) {

        if (returnRepository.existsByLoanId(returnRequest.loanId())) {
            throw new DuplicateResourceException(
                    "El préstamo ya tiene una devolución registrada"
            );
        }

        LocalDate currentDate = LocalDate.now();

        boolean delayed = currentDate.isAfter(returnRequest.dueDate());

        BookReturn bookReturn = new BookReturn();
        bookReturn.setLoanId(returnRequest.loanId());
        bookReturn.setReturnDate(currentDate);
        bookReturn.setDelayed(delayed);

        BookReturn savedReturn = returnRepository.save(bookReturn);

        return new ApiResponse<>(
                true,
                mapToResponse(savedReturn),
                delayed
                        ? "Devolución registrada con atraso"
                        : "Devolución registrada con éxito"
        );
    }

    @Override
    public ApiResponse<ReturnResponse> getReturnByLoanId(UUID loanId) {

        BookReturn bookReturn = returnRepository.findByLoanId(loanId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró una devolución para el préstamo: " + loanId
                ));

        return new ApiResponse<>(
                true,
                mapToResponse(bookReturn),
                "Devolución encontrada con éxito"
        );
    }

    private ReturnResponse mapToResponse(BookReturn bookReturn) {

        return new ReturnResponse(
                bookReturn.getId(),
                bookReturn.getLoanId(),
                bookReturn.getReturnDate(),
                bookReturn.isDelayed()
        );
    }
}