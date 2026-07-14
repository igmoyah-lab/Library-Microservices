
package com.library.returns.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.library.returns.entity.BookReturn;

@DataJpaTest
@ActiveProfiles("test")
class ReturnRepositoryTest {

    @Autowired
    private ReturnRepository returnRepository;

    @Test
    void findByLoanId_shouldReturnBookReturn_whenLoanIdExists() {
        UUID loanId = UUID.randomUUID();

        BookReturn bookReturn = createBookReturn(loanId);

        BookReturn savedBookReturn =
                returnRepository.saveAndFlush(bookReturn);

        Optional<BookReturn> result =
                returnRepository.findByLoanId(loanId);

        assertTrue(result.isPresent());
        assertEquals(savedBookReturn.getId(), result.get().getId());
        assertEquals(loanId, result.get().getLoanId());
    }

    @Test
    void findByLoanId_shouldReturnEmpty_whenLoanIdDoesNotExist() {
        UUID nonexistentLoanId = UUID.randomUUID();

        Optional<BookReturn> result =
                returnRepository.findByLoanId(nonexistentLoanId);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByLoanId_shouldNotReturnAnotherLoan() {
        UUID savedLoanId = UUID.randomUUID();
        UUID searchedLoanId = UUID.randomUUID();

        BookReturn bookReturn = createBookReturn(savedLoanId);

        returnRepository.saveAndFlush(bookReturn);

        Optional<BookReturn> result =
                returnRepository.findByLoanId(searchedLoanId);

        assertTrue(result.isEmpty());
    }

    @Test
    void existsByLoanId_shouldReturnTrue_whenLoanIdExists() {
        UUID loanId = UUID.randomUUID();

        BookReturn bookReturn = createBookReturn(loanId);

        returnRepository.saveAndFlush(bookReturn);

        boolean result =
                returnRepository.existsByLoanId(loanId);

        assertTrue(result);
    }

    @Test
    void existsByLoanId_shouldReturnFalse_whenLoanIdDoesNotExist() {
        UUID nonexistentLoanId = UUID.randomUUID();

        boolean result =
                returnRepository.existsByLoanId(nonexistentLoanId);

        assertFalse(result);
    }

    @Test
    void existsByLoanId_shouldReturnFalse_forAnotherLoanId() {
        UUID savedLoanId = UUID.randomUUID();
        UUID searchedLoanId = UUID.randomUUID();

        BookReturn bookReturn = createBookReturn(savedLoanId);

        returnRepository.saveAndFlush(bookReturn);

        boolean result =
                returnRepository.existsByLoanId(searchedLoanId);

        assertFalse(result);
    }

    private BookReturn createBookReturn(UUID loanId) {
        BookReturn bookReturn = new BookReturn();

        bookReturn.setLoanId(loanId);
        bookReturn.setReturnDate(LocalDate.now());

        return bookReturn;
    }
}

