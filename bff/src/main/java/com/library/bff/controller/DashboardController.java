package com.library.bff.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.UserDashboardResponse;
import com.library.bff.services.DashboardService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
            DashboardService dashboardService
    ) {
        this.dashboardService = dashboardService;
    }

    /**
     * Obtiene el dashboard compuesto de un usuario.
     *
     * @param userId identificador del usuario
     * @return información combinada del usuario
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<UserDashboardResponse>
            getUserDashboard(
                    @PathVariable UUID userId
            ) {
        return dashboardService.getUserDashboard(userId);
    }
}
