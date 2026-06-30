package com.library.auth.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.library.auth.dto.AuthResponse;
import com.library.auth.dto.LoginRequest;
import com.library.auth.dto.RegisterRequest;
import com.library.auth.service.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(authService);
    }

    @Test
    void login_shouldCallServiceAndReturnToken() {
        LoginRequest request = new LoginRequest("test@correo.com", "123456");
        AuthResponse expectedResponse = new AuthResponse("jwt-token");

        when(authService.login(request)).thenReturn(expectedResponse);

        AuthResponse response = authController.login(request);

        assertEquals("jwt-token", response.token());
        verify(authService).login(request);
    }

    @Test
    void register_shouldCallServiceAndReturnToken() {
        RegisterRequest request = new RegisterRequest("nuevo@correo.com", "123456");
        AuthResponse expectedResponse = new AuthResponse("jwt-token");

        when(authService.register(request)).thenReturn(expectedResponse);

        AuthResponse response = authController.register(request);

        assertEquals("jwt-token", response.token());
        verify(authService).register(request);
    }
}
