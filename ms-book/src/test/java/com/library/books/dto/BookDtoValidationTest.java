package com.library.books.dto;


import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class BookDtoValidationTest {

    private Validator validator;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validDto_shouldHaveNoViolations() {
        BookDto dto = new BookDto(
                UUID.randomUUID(),
                "El Principito",
                "Antoine de Saint-Exupéry",
                "Literatura",
                "123456789"
        );

        var violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidDto_shouldReportExpectedFields() {
        BookDto dto = new BookDto(
                UUID.randomUUID(),
                "",
                "",
                "",
                ""
        );

        var violations = validator.validate(dto);

        Set<String> fields = violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertEquals(4, violations.size());
        assertTrue(fields.contains("title"));
        assertTrue(fields.contains("author"));
        assertTrue(fields.contains("category"));
        assertTrue(fields.contains("isbn"));
    }
}