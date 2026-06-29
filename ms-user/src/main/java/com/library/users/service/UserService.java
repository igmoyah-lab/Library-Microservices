package com.library.users.service;

import java.util.List;
import java.util.UUID;

import com.library.users.dto.ApiResponse;
import com.library.users.dto.UserDto;

public interface UserService {

    ApiResponse<List<UserDto>> getAllProfiles();

    ApiResponse<UserDto> updateProfile(UserDto userDto);

    ApiResponse<Void> deleteProfile(UUID id);
}