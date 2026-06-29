package com.library.bff.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class CurrentUserServiceTest {

    private final CurrentUserService currentUserService = new CurrentUserService();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserEmail_whenAuthenticated_shouldReturnEmail() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "test@correo.com",
                null,
                Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String email = currentUserService.getCurrentUserEmail();

        assertEquals("test@correo.com", email);
    }

    @Test
    void getCurrentUserEmail_whenNotAuthenticated_shouldThrowException() {
        assertThrows(IllegalStateException.class, () -> currentUserService.getCurrentUserEmail());
    }
}
