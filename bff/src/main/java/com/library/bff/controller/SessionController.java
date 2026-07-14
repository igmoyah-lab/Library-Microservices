package com.library.bff.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.SessionResponse;
import com.library.bff.security.CurrentUserService;

@RestController
@RequestMapping("/session")
public class SessionController {

    private final CurrentUserService currentUserService;

    public SessionController(
            CurrentUserService currentUserService
    ) {
        this.currentUserService = currentUserService;
    }

    /**
     * Obtiene la información del usuario autenticado
     * mediante el token JWT enviado al BFF.
     *
     * @return información de la sesión actual
     */
    @GetMapping
    public ApiResponse<SessionResponse> getCurrentSession() {
        String email =
                currentUserService.getCurrentUserEmail();

        SessionResponse session =
                new SessionResponse(
                        email,
                        true
                );

        return new ApiResponse<>(
                true,
                session,
                "Sesión autenticada correctamente"
        );
    }
}
