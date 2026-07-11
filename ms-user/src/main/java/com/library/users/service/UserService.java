package com.library.users.service;

import java.util.List;
import java.util.UUID;

import com.library.users.dto.ApiResponse;
import com.library.users.dto.UserDto;

public interface UserService {

    /**
     * Obtiene todos los perfiles registrados.
     *
     * @return respuesta con la lista de perfiles
     */
    ApiResponse<List<UserDto>> getAllProfiles();

    /**
     * Obtiene un perfil mediante su identificador.
     *
     * @param id identificador del perfil
     * @return respuesta con el perfil encontrado
     */
    ApiResponse<UserDto> getProfileById(UUID id);

    /**
     * Crea o actualiza el perfil asociado a un correo.
     *
     * @param userDto datos del perfil
     * @return respuesta con el perfil guardado
     */
    ApiResponse<UserDto> updateProfile(UserDto userDto);

    /**
     * Elimina un perfil mediante su identificador.
     *
     * @param id identificador del perfil
     * @return respuesta sin contenido
     */
    ApiResponse<Void> deleteProfile(UUID id);
}