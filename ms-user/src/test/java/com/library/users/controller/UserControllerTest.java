package com.library.users.controller;

import com.library.users.dto.ApiResponse;
import com.library.users.dto.UserDto;
import com.library.users.service.UserService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Test
    void getAllProfiles_shouldReturnProfiles() {
        UserController userController = new UserController(userService);

        UserDto user = response(
                UUID.randomUUID(),
                "usuario@test.com",
                "Usuario Test",
                "912345678",
                "Santiago"
        );

        ApiResponse<List<UserDto>> apiResponse = new ApiResponse<>(
                true,
                "Perfiles obtenidos correctamente",
                List.of(user)
        );

        when(userService.getAllProfiles()).thenReturn(apiResponse);

        ApiResponse<List<UserDto>> result = userController.getAllProfiles();

        assertTrue(result.success());
        assertEquals("Perfiles obtenidos correctamente", result.message());
        assertNotNull(result.data());
        assertEquals(1, result.data().size());
        assertEquals("Usuario Test", result.data().get(0).fullName());
    }

    @Test
    void updateProfile_shouldReturnUpdatedProfile() {
        UserController userController = new UserController(userService);

        UserDto user = response(
                UUID.randomUUID(),
                "usuario@test.com",
                "Usuario Actualizado",
                "987654321",
                "Valparaíso"
        );

        ApiResponse<UserDto> apiResponse = new ApiResponse<>(
                true,
                "Perfil actualizado correctamente",
                user
        );

        when(userService.updateProfile(any())).thenReturn(apiResponse);

        ApiResponse<UserDto> result = userController.updateProfile(user);

        assertTrue(result.success());
        assertEquals("Perfil actualizado correctamente", result.message());
        assertNotNull(result.data());
        assertEquals("Usuario Actualizado", result.data().fullName());

        verify(userService).updateProfile(any());
    }

    @Test
    void deleteProfile_shouldReturnSuccessResponse() {
        UserController userController = new UserController(userService);

        ApiResponse<Void> apiResponse = new ApiResponse<>(
                true,
                "Perfil eliminado correctamente",
                null
        );

        when(userService.deleteProfile(any())).thenReturn(apiResponse);

        ApiResponse<Void> result = userController.deleteProfile(UUID.randomUUID());

        assertTrue(result.success());
        assertEquals("Perfil eliminado correctamente", result.message());
        assertEquals(null, result.data());

        verify(userService).deleteProfile(any());
    }

    private UserDto response(UUID id, String authEmail, String fullName, String phone, String address) {
        return new UserDto(id, authEmail, fullName, phone, address);
    }
}