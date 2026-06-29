package com.library.bff.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.library.bff.client.UserClient;
import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.ProfileUpdateRequest;
import com.library.bff.dto.UserDto;

@Service
public class UserService {

    private final UserClient userClient;

    public UserService(UserClient userClient) {
        this.userClient = userClient;
    }

    public ApiResponse<List<UserDto>> getAllProfiles() {
        return userClient.getAllProfiles();
    }

    public ApiResponse<UserDto> updateProfile(ProfileUpdateRequest request) {
        return userClient.updateProfile(request);
    }

    public ApiResponse<Void> deleteProfile(UUID id) {
        return userClient.deleteProfile(id);
    }
}