package com.library.bff.client;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.ProfileUpdateRequest;
import com.library.bff.dto.UserDto;
import com.library.bff.security.CurrentUserService;

@Component
public class UserClient {

    private final RestClient restClient;
    private final CurrentUserService currentUserService;

    public UserClient(
            @Value("${user.service.base-url}") String userBaseUrl,
            CurrentUserService currentUserService
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(userBaseUrl)
                .build();

        this.currentUserService = currentUserService;
    }

    public ApiResponse<List<UserDto>> getAllProfiles() {
        return restClient.get()
                .uri("/users")
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<List<UserDto>>>() {});
    }


    public ApiResponse<UserDto> updateProfile(ProfileUpdateRequest request) {
        String email = currentUserService.getCurrentUserEmail();

        UserDto userDto = new UserDto(
                null,
                email,
                request.fullName(),
                request.phone(),
                request.address()
        );

        return restClient.put()
                .uri("/users/profile")
                .body(userDto)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<UserDto>>() {});
    }

    public ApiResponse<Void> deleteProfile(UUID id) {
        return restClient.delete()
                .uri("/users/{id}", id)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<Void>>() {});
    }
}