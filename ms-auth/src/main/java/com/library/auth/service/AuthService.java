package com.library.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.library.auth.domain.User;
import com.library.auth.dto.AuthResponse;
import com.library.auth.dto.LoginRequest;
import com.library.auth.dto.RegisterRequest;
import com.library.auth.exception.DuplicateEmailException;
import com.library.auth.exception.InvalidCredentialsException;
import com.library.auth.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Autentica un usuario mediante correo y contraseña.
     *
     * @param request credenciales ingresadas
     * @return token JWT para el usuario autenticado
     * @throws InvalidCredentialsException si el correo no existe,
     *         el usuario está inactivo o la contraseña es incorrecta
     */
    public AuthResponse login(LoginRequest request) {
        User user = userRepository
                .findByEmailAndActiveTrue(request.email())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Credenciales inválidas"
                        )
                );

        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword()
        )) {
            throw new InvalidCredentialsException(
                    "Credenciales inválidas"
            );
        }

        String token = jwtService.generateToken(
                user.getEmail()
        );

        return new AuthResponse(token);
    }

    /**
     * Registra un usuario nuevo y genera su token JWT.
     *
     * @param request datos de la nueva cuenta
     * @return token JWT para el usuario registrado
     * @throws DuplicateEmailException si el correo
     *         ya se encuentra registrado
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsById(request.email())) {
            throw new DuplicateEmailException(
                    "El email ya está registrado"
            );
        }

        String encodedPassword =
                passwordEncoder.encode(
                        request.password()
                );

        User user = new User(
                request.email(),
                encodedPassword
        );

        userRepository.save(user);

        String token = jwtService.generateToken(
                user.getEmail()
        );

        return new AuthResponse(token);
    }
}
