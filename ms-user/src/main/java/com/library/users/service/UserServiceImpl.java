package com.library.users.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.library.users.dto.ApiResponse;
import com.library.users.dto.UserDto;
import com.library.users.entity.User;
import com.library.users.exception.ResourceNotFoundException;
import com.library.users.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

private final UserRepository userRepository;

public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
}

/**
* Obtiene todos los perfiles de usuario registrados.
*
* @return respuesta con la lista de perfiles
*/
@Override
public ApiResponse<List<UserDto>> getAllProfiles() {
        List<UserDto> profiles = userRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();

        return new ApiResponse<>(
                true,
                "Perfiles obtenidos correctamente",
                profiles
        );
}

/**
     * Obtiene un perfil mediante su identificador.
     *
     * @param id identificador del perfil
     * @return respuesta con el perfil encontrado
     * @throws ResourceNotFoundException si el perfil no existe
     */
@Override
public ApiResponse<UserDto> getProfileById(UUID id) {
        User profile = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Perfil no encontrado con id: " + id
                        )
                );

        return new ApiResponse<>(
                true,
                "Perfil obtenido correctamente",
                mapToDto(profile)
        );
}

/**
     * Crea o actualiza un perfil utilizando el correo del usuario.
     *
     * @param userDto datos del perfil
     * @return respuesta con el perfil guardado
     */
@Override
public ApiResponse<UserDto> updateProfile(UserDto userDto) {
        User profile = userRepository
                .findByAuthEmailIgnoreCase(userDto.authEmail())
                .orElse(new User());

        boolean isNewProfile = profile.getId() == null;

        profile.setAuthEmail(userDto.authEmail());
        profile.setFullName(userDto.fullName());
        profile.setPhone(userDto.phone());
        profile.setAddress(userDto.address());

        User savedProfile = userRepository.save(profile);

        String message = isNewProfile
                ? "Perfil completado correctamente"
                : "Perfil actualizado correctamente";

        return new ApiResponse<>(
                true,
                message,
                mapToDto(savedProfile)
        );
}

/**
     * Elimina un perfil mediante su identificador.
     *
     * @param id identificador del perfil
     * @return respuesta sin contenido
     * @throws ResourceNotFoundException si el perfil no existe
     */
@Override
public ApiResponse<Void> deleteProfile(UUID id) {
        User profile = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Perfil no encontrado con id: " + id
                        )
                );

        userRepository.delete(profile);

        return new ApiResponse<>(
                true,
                "Perfil eliminado correctamente",
                null
        );
}

/**
     * Convierte una entidad User en un objeto UserDto.
     *
     * @param profile entidad del perfil
     * @return datos del perfil convertidos a DTO
     */
private UserDto mapToDto(User profile) {
        return new UserDto(
                profile.getId(),
                profile.getAuthEmail(),
                profile.getFullName(),
                profile.getPhone(),
                profile.getAddress()
        );
}
}