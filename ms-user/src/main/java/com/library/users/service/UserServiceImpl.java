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

    @Override
    public ApiResponse<Void> deleteProfile(UUID id) {
        User profile = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado con id: " + id));

        userRepository.delete(profile);

        return new ApiResponse<>(
                true,
                "Perfil eliminado correctamente",
                null
        );
    }

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