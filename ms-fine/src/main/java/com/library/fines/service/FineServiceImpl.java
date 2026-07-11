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
import com.library.fines.exception.BusinessRuleException;
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

/**
 * Crea una multa calculando el monto según
 * los días de atraso informados.
 *
 * @param fineRequest datos necesarios para crear la multa
 * @return respuesta con la multa creada
 * @throws DuplicateResourceException si el préstamo
 *         ya posee una multa
 */
@Override
public ApiResponse<FineResponse> createFine(
        FineRequest fineRequest
) {
if (fineRepository.existsByLoanId(
        fineRequest.loanId()
)) {
        throw new DuplicateResourceException(
                "El préstamo ya tiene una multa registrada"
        );
}

BigDecimal amount = DAILY_FINE.multiply(
        BigDecimal.valueOf(
                fineRequest.delayedDays()
        )
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

/**
 * Obtiene una multa mediante su identificador.
 *
 * @param id identificador de la multa
 * @return respuesta con la multa encontrada
 * @throws ResourceNotFoundException si la multa no existe
 */
@Override
public ApiResponse<FineResponse> getFineById(UUID id) {
Fine fine = findFineById(id);

return new ApiResponse<>(
        true,
        mapToResponse(fine),
        "Multa obtenida con éxito"
);
}

/**
 * Obtiene todas las multas pertenecientes
 * a un usuario.
 *
 * @param userId identificador del usuario
 * @return respuesta con las multas encontradas
 * @throws ResourceNotFoundException si el usuario
 *         no posee multas
 */
@Override
public ApiResponse<List<FineResponse>> getFinesByUserId(
        UUID userId
) {
List<Fine> fines =
        fineRepository.findByUserId(userId);

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

/**
 * Marca una multa pendiente como pagada.
 *
 * @param id identificador de la multa
 * @return respuesta con la multa pagada
 * @throws ResourceNotFoundException si la multa no existe
 * @throws BusinessRuleException si la multa ya está pagada
 */
@Override
public ApiResponse<FineResponse> payFine(UUID id) {
Fine fine = findFineById(id);

if (fine.getStatus() == FineStatus.PAID) {
        throw new BusinessRuleException(
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

/**
 * Obtiene la cantidad total de multas registradas.
 *
 * @return cantidad total de multas
 */
@Override
public long countFines() {
return fineRepository.count();
}

/**
 * Obtiene la cantidad de multas pendientes.
 *
 * @return cantidad de multas pendientes
 */
@Override
public long countPendingFines() {
return fineRepository.countByStatus(
        FineStatus.PENDING
);
}

/**
 * Obtiene la cantidad de multas pagadas.
 *
 * @return cantidad de multas pagadas
 */
@Override
public long countPaidFines() {
return fineRepository.countByStatus(
        FineStatus.PAID
);
}

/**
 * Busca internamente una multa mediante su ID.
 *
 * @param id identificador de la multa
 * @return multa encontrada
 * @throws ResourceNotFoundException si la multa no existe
 */
private Fine findFineById(UUID id) {
return fineRepository.findById(id)
        .orElseThrow(() ->
                new ResourceNotFoundException(
                        "No se encontró la multa con id: "
                                + id
                )
        );
}

/**
 * Convierte una entidad Fine en FineResponse.
 *
 * @param fine entidad de la multa
 * @return DTO con los datos de la multa
 */
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
}