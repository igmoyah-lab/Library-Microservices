package com.library.bff.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.library.bff.client.AuthClient;
import com.library.bff.dto.AuthResponse;
import com.library.bff.dto.LoginRequest;
import com.library.bff.dto.RegisterRequest;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthClient authClient;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(authClient);
    }

    @Test
    void login_shouldDelegateToAuthClient() {
        LoginRequest request = new LoginRequest("test@correo.com", "123456");
        AuthResponse expectedResponse = new AuthResponse("jwt-token");

        when(authClient.login(request)).thenReturn(expectedResponse);

        AuthResponse response = authService.login(request);

        assertEquals("jwt-token", response.token());
        verify(authClient).login(request);
    }

    @Test
    void register_shouldDelegateToAuthClient() {
        RegisterRequest request = new RegisterRequest("nuevo@correo.com", "123456");
        AuthResponse expectedResponse = new AuthResponse("jwt-token");

        when(authClient.register(request)).thenReturn(expectedResponse);

        AuthResponse response = authService.register(request);

        assertEquals("jwt-token", response.token());
        verify(authClient).register(request);
    }
}
