package com.library.loans.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.library.loans.client.BookClient;
import com.library.loans.client.UserClient;
import com.library.loans.client.dto.BookClientResponse;
import com.library.loans.dto.ApiResponse;
import com.library.loans.dto.LoanRequest;
import com.library.loans.dto.LoanResponse;
import com.library.loans.entity.Loan;
import com.library.loans.entity.LoanStatus;
import com.library.loans.exception.BusinessRuleException;
import com.library.loans.exception.ResourceNotFoundException;
import com.library.loans.repository.LoanRepository;

@Service
public class LoanServiceImpl implements LoanService {

private final LoanRepository loanRepository;
private final UserClient userClient;
private final BookClient bookClient;

public LoanServiceImpl(
        LoanRepository loanRepository,
        UserClient userClient,
        BookClient bookClient
) {
    this.loanRepository = loanRepository;
    this.userClient = userClient;
    this.bookClient = bookClient;
}

/**
 * Crea un préstamo después de validar que el usuario
 * y el libro existan, que el libro esté disponible
 * y que no tenga otro préstamo activo.
 *
 * @param loanRequest datos necesarios para crear el préstamo
 * @return respuesta con el préstamo creado
 * @throws ResourceNotFoundException si el usuario o libro no existe
 * @throws BusinessRuleException si el libro no está disponible
 *         o ya tiene un préstamo activo
 */
@Override
public ApiResponse<LoanResponse> createLoan(
        LoanRequest loanRequest
) {
    UUID userId = loanRequest.userId();
    UUID bookId = loanRequest.bookId();

    /*
    * Valida que el usuario exista consultando
    * directamente al microservicio ms-user.
    */
    userClient.getUserById(userId);

    /*
    * Obtiene el libro desde ms-book para validar
    * su existencia y disponibilidad.
    */
    BookClientResponse book =
            bookClient.getBookById(bookId);

    boolean hasActiveLoan =
            loanRepository.existsByBookIdAndStatus(
                    bookId,
                    LoanStatus.ACTIVE
            );

    if (hasActiveLoan) {
        throw new BusinessRuleException(
                "El libro ya posee un préstamo activo"
        );
    }

    if (!Boolean.TRUE.equals(book.available())) {
        throw new BusinessRuleException(
                "El libro no se encuentra disponible"
        );
    }

    LocalDate currentDate = LocalDate.now();

    Loan loan = new Loan();
    loan.setUserId(userId);
    loan.setBookId(bookId);
    loan.setLoanDate(currentDate);
    loan.setDueDate(currentDate.plusDays(7));
    loan.setStatus(LoanStatus.ACTIVE);

    Loan savedLoan = loanRepository.save(loan);

    try {
        /*
        * Después de guardar el préstamo,
        * el libro queda marcado como no disponible.
        */
        bookClient.updateAvailability(
                bookId,
                false
        );

    } catch (RuntimeException exception) {
        /*
        * Si ms-book no puede actualizarse,
        * se elimina el préstamo para evitar
        * información inconsistente.
        */
        loanRepository.delete(savedLoan);

        throw exception;
    }

    return new ApiResponse<>(
            true,
            mapToResponse(savedLoan),
            "Préstamo creado con éxito"
    );
}

/**
 * Obtiene un préstamo mediante su identificador.
 *
 * @param loanId identificador del préstamo
 * @return respuesta con el préstamo encontrado
 * @throws ResourceNotFoundException si el préstamo no existe
 */
@Override
public ApiResponse<LoanResponse> getLoanById(
        UUID loanId
) {
    Loan loan = findLoanById(loanId);

    return new ApiResponse<>(
            true,
            mapToResponse(loan),
            "Préstamo obtenido con éxito"
    );
}

/**
 * Obtiene todos los préstamos pertenecientes
 * a un usuario.
 *
 * @param userId identificador del usuario
 * @return respuesta con los préstamos encontrados
 * @throws ResourceNotFoundException si el usuario
 *         no posee préstamos
 */
@Override
public ApiResponse<List<LoanResponse>> getLoansByUserId(
        UUID userId
) {
    List<Loan> loans =
            loanRepository.findByUserId(userId);

    if (loans.isEmpty()) {
        throw new ResourceNotFoundException(
                "No se encontraron préstamos para "
                        + "el usuario con id: "
                        + userId
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

/**
 * Marca un préstamo activo como devuelto.
 *
 * @param loanId identificador del préstamo
 * @return respuesta con el préstamo actualizado
 * @throws ResourceNotFoundException si el préstamo no existe
 * @throws BusinessRuleException si el préstamo ya fue devuelto
 */
@Override
public ApiResponse<LoanResponse> markLoanAsReturned(
        UUID loanId
) {
    Loan loan = findLoanById(loanId);

    if (loan.getStatus() == LoanStatus.RETURNED) {
        throw new BusinessRuleException(
                "El préstamo ya se encuentra devuelto"
        );
    }

    loan.setStatus(LoanStatus.RETURNED);

    Loan updatedLoan = loanRepository.save(loan);

    return new ApiResponse<>(
            true,
            mapToResponse(updatedLoan),
            "Préstamo marcado como devuelto"
    );
}

/**
 * Obtiene la cantidad total de préstamos registrados.
 *
 * @return cantidad total de préstamos
 */
@Override
public long countLoans() {
    return loanRepository.count();
}

/**
 * Busca internamente un préstamo mediante su identificador.
 *
 * @param loanId identificador del préstamo
 * @return préstamo encontrado
 * @throws ResourceNotFoundException si el préstamo no existe
 */
private Loan findLoanById(UUID loanId) {
    return loanRepository.findById(loanId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Préstamo no encontrado con id: "
                                    + loanId
                    )
            );
}

/**
 * Convierte una entidad Loan en un LoanResponse.
 *
 * @param loan entidad del préstamo
 * @return DTO con los datos del préstamo
 */
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