package com.library.bff.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;

import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.ProfileUpdateRequest;
import com.library.bff.dto.UserDto;
import com.library.bff.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<List<UserDto>> getAllProfiles() {
        return userService.getAllProfiles();
    }


    @PutMapping("/profile")
    public ApiResponse<UserDto> updateProfile(@RequestBody ProfileUpdateRequest request) {
        return userService.updateProfile(request);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProfile(@PathVariable UUID id) {
        return userService.deleteProfile(id);
    }
}