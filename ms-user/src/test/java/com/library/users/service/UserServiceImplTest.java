package com.library.users.service;

import com.library.users.dto.ApiResponse;
import com.library.users.dto.UserDto;
import com.library.users.entity.User;
import com.library.users.exception.ResourceNotFoundException;
import com.library.users.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void getAllProfiles_shouldReturnListOfProfiles() {
        UserServiceImpl userService = new UserServiceImpl(userRepository);

        User user1 = entity(
                UUID.randomUUID(),
                "user1@test.com",
                "Usuario Uno",
                "912345678",
                "Santiago"
        );

        User user2 = entity(
                UUID.randomUUID(),
                "user2@test.com",
                "Usuario Dos",
                "987654321",
                "Valparaíso"
        );

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        ApiResponse<List<UserDto>> result = userService.getAllProfiles();

        assertTrue(result.success());
        assertEquals("Perfiles obtenidos correctamente", result.message());
        assertNotNull(result.data());
        assertEquals(2, result.data().size());
        assertEquals("Usuario Uno", result.data().get(0).fullName());
    }

    
    @Test
    void updateProfile_shouldCreateProfileWhenEmailDoesNotExist() {
    UserServiceImpl userService = new UserServiceImpl(userRepository);

    UserDto request = new UserDto(
            null,
            "nuevo@test.com",
            "Usuario Nuevo",
            "912345678",
            "Santiago"
    );

    UUID id = UUID.randomUUID();

    when(userRepository.findByAuthEmailIgnoreCase("nuevo@test.com"))
            .thenReturn(Optional.empty());

    when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
        User userToSave = invocation.getArgument(0);
        userToSave.setId(id);
        return userToSave;
    });

    ApiResponse<UserDto> result = userService.updateProfile(request);

    assertTrue(result.success());
    assertEquals("Perfil completado correctamente", result.message());
    assertNotNull(result.data());
    assertEquals("nuevo@test.com", result.data().authEmail());
    assertEquals("Usuario Nuevo", result.data().fullName());
    assertEquals("912345678", result.data().phone());
    assertEquals("Santiago", result.data().address());

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(captor.capture());

    assertEquals("nuevo@test.com", captor.getValue().getAuthEmail());
    assertEquals("Usuario Nuevo", captor.getValue().getFullName());
    assertEquals("912345678", captor.getValue().getPhone());
    assertEquals("Santiago", captor.getValue().getAddress());
}

    @Test
void updateProfile_shouldUpdateProfileWhenEmailExists() {
    UserServiceImpl userService = new UserServiceImpl(userRepository);

    UUID id = UUID.randomUUID();

    User existingUser = entity(
            id,
            "existente@test.com",
            "Nombre Antiguo",
            "111111111",
            "Dirección Antigua"
    );

    UserDto request = new UserDto(
            id,
            "existente@test.com",
            "Nombre Actualizado",
            "999999999",
            "Dirección Nueva"
    );

    when(userRepository.findByAuthEmailIgnoreCase("existente@test.com"))
            .thenReturn(Optional.of(existingUser));

    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ApiResponse<UserDto> result = userService.updateProfile(request);

    assertTrue(result.success());
    assertEquals("Perfil actualizado correctamente", result.message());
    assertNotNull(result.data());
    assertEquals("existente@test.com", result.data().authEmail());
    assertEquals("Nombre Actualizado", result.data().fullName());
    assertEquals("999999999", result.data().phone());
    assertEquals("Dirección Nueva", result.data().address());

    verify(userRepository).save(existingUser);
}


    @Test
    void deleteProfile_shouldDeleteWhenExists() {
        UserServiceImpl userService = new UserServiceImpl(userRepository);

        UUID id = UUID.randomUUID();

        User user = entity(
                id,
                "delete@test.com",
                "Usuario Delete",
                "900000000",
                "Santiago"
        );

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        ApiResponse<Void> result = userService.deleteProfile(id);

        assertTrue(result.success());
        assertEquals("Perfil eliminado correctamente", result.message());

        verify(userRepository).delete(user);
    }

    @Test
    void deleteProfile_shouldThrowExceptionWhenMissing() {
        UserServiceImpl userService = new UserServiceImpl(userRepository);

        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException result = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.deleteProfile(id)
        );

        assertEquals("Perfil no encontrado con id: " + id, result.getMessage());
    }

    private User entity(UUID id, String authEmail, String fullName, String phone, String address) {
        User user = new User();
        user.setId(id);
        user.setAuthEmail(authEmail);
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setAddress(address);
        return user;
    }
}