package com.library.bff.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.library.bff.client.UserClient;
import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.ProfileUpdateRequest;
import com.library.bff.dto.UserDto;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserClient userClient;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userClient);
    }

    @Test
    void getAllProfiles_shouldDelegateToUserClient() {
        UserDto user = new UserDto(UUID.randomUUID(), "test@correo.com", "Usuario Test", "912345678", "Santiago");
        ApiResponse<List<UserDto>> expectedResponse = new ApiResponse<>(true, List.of(user), "Perfiles encontrados");

        when(userClient.getAllProfiles()).thenReturn(expectedResponse);

        ApiResponse<List<UserDto>> response = userService.getAllProfiles();

        assertEquals(expectedResponse, response);
        verify(userClient).getAllProfiles();
    }

    @Test
    void updateProfile_shouldDelegateToUserClient() {
        ProfileUpdateRequest request = new ProfileUpdateRequest("Usuario Test", "912345678", "Santiago");
        UserDto user = new UserDto(UUID.randomUUID(), "test@correo.com", "Usuario Test", "912345678", "Santiago");
        ApiResponse<UserDto> expectedResponse = new ApiResponse<>(true, user, "Perfil actualizado");

        when(userClient.updateProfile(request)).thenReturn(expectedResponse);

        ApiResponse<UserDto> response = userService.updateProfile(request);

        assertEquals(expectedResponse, response);
        verify(userClient).updateProfile(request);
    }

    @Test
    void deleteProfile_shouldDelegateToUserClient() {
        UUID id = UUID.randomUUID();
        ApiResponse<Void> expectedResponse = new ApiResponse<>(true, null, "Perfil eliminado");

        when(userClient.deleteProfile(id)).thenReturn(expectedResponse);

        ApiResponse<Void> response = userService.deleteProfile(id);

        assertEquals(expectedResponse, response);
        verify(userClient).deleteProfile(id);
    }
}
