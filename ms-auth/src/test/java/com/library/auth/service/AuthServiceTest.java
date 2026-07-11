package com.library.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.library.auth.domain.User;
import com.library.auth.dto.AuthResponse;
import com.library.auth.dto.LoginRequest;
import com.library.auth.dto.RegisterRequest;
import com.library.auth.exception.DuplicateEmailException;
import com.library.auth.exception.InvalidCredentialsException;
import com.library.auth.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepository,
                passwordEncoder,
                jwtService
        );
    }

    @Test
    void register_whenEmailDoesNotExist_shouldSaveUserAndReturnToken() {
        RegisterRequest request =
                new RegisterRequest(
                        "test@correo.com",
                        "123456"
                );

        when(
                userRepository.existsById(
                        "test@correo.com"
                )
        ).thenReturn(false);

        when(passwordEncoder.encode("123456"))
                .thenReturn("password-encriptada");

        when(
                jwtService.generateToken(
                        "test@correo.com"
                )
        ).thenReturn("jwt-token");

        AuthResponse response =
                authService.register(request);

        assertEquals(
                "jwt-token",
                response.token()
        );

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository)
                .save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals(
                "test@correo.com",
                savedUser.getEmail()
        );

        assertEquals(
                "password-encriptada",
                savedUser.getPassword()
        );

        assertTrue(savedUser.isActive());
    }

    @Test
    void register_whenEmailAlreadyExists_shouldThrowDuplicateEmail() {
        RegisterRequest request =
                new RegisterRequest(
                        "test@correo.com",
                        "123456"
                );

        when(
                userRepository.existsById(
                        "test@correo.com"
                )
        ).thenReturn(true);

        DuplicateEmailException exception =
                assertThrows(
                        DuplicateEmailException.class,
                        () -> authService.register(request)
                );

        assertEquals(
                "El email ya está registrado",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));

        verify(passwordEncoder, never())
                .encode(any());
    }

    @Test
    void login_whenCredentialsAreValid_shouldReturnToken() {
        LoginRequest request =
                new LoginRequest(
                        "test@correo.com",
                        "123456"
                );

        User user = new User(
                "test@correo.com",
                "password-encriptada"
        );

        when(
                userRepository.findByEmailAndActiveTrue(
                        "test@correo.com"
                )
        ).thenReturn(Optional.of(user));

        when(
                passwordEncoder.matches(
                        "123456",
                        "password-encriptada"
                )
        ).thenReturn(true);

        when(
                jwtService.generateToken(
                        "test@correo.com"
                )
        ).thenReturn("jwt-token");

        AuthResponse response =
                authService.login(request);

        assertEquals(
                "jwt-token",
                response.token()
        );
    }

    @Test
    void login_whenUserDoesNotExist_shouldThrowInvalidCredentials() {
        LoginRequest request =
                new LoginRequest(
                        "noexiste@correo.com",
                        "123456"
                );

        when(
                userRepository.findByEmailAndActiveTrue(
                        "noexiste@correo.com"
                )
        ).thenReturn(Optional.empty());

        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> authService.login(request)
                );

        assertEquals(
                "Credenciales inválidas",
                exception.getMessage()
        );

        verify(passwordEncoder, never())
                .matches(any(), any());
    }

    @Test
    void login_whenPasswordIsInvalid_shouldThrowInvalidCredentials() {
        LoginRequest request =
                new LoginRequest(
                        "test@correo.com",
                        "mala"
                );

        User user = new User(
                "test@correo.com",
                "password-encriptada"
        );

        when(
                userRepository.findByEmailAndActiveTrue(
                        "test@correo.com"
                )
        ).thenReturn(Optional.of(user));

        when(
                passwordEncoder.matches(
                        "mala",
                        "password-encriptada"
                )
        ).thenReturn(false);

        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> authService.login(request)
                );

        assertEquals(
                "Credenciales inválidas",
                exception.getMessage()
        );

        verify(jwtService, never())
                .generateToken(any());
    }
}