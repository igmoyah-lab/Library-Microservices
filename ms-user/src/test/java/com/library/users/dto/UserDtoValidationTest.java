package com.library.users.dto;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class UserDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validDto_shouldHaveNoViolations() {
        UserDto dto = new UserDto(
                UUID.randomUUID(),
                "usuario@test.com",
                "Usuario Test",
                "912345678",
                "Santiago"
        );

        var violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidDto_shouldReportExpectedFields() {
        UserDto dto = new UserDto(
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

        assertTrue(fields.contains("authEmail"));
        assertTrue(fields.contains("fullName"));
        assertTrue(fields.contains("phone"));
        assertTrue(fields.contains("address"));
    }

    @Test
    void invalidEmail_shouldReportAuthEmailField() {
        UserDto dto = new UserDto(
                UUID.randomUUID(),
                "correo-invalido",
                "Usuario Test",
                "912345678",
                "Santiago"
        );

        var violations = validator.validate(dto);

        Set<String> fields = violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertTrue(fields.contains("authEmail"));
    }
}