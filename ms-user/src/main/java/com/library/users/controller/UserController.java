package com.library.users.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.users.dto.ApiResponse;
import com.library.users.dto.UserDto;
import com.library.users.service.UserService;

import jakarta.validation.Valid;

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

    @GetMapping("/{id}")
    public ApiResponse<UserDto> getProfileById(@PathVariable UUID id) {
        return userService.getProfileById(id);
    }

    @PutMapping("/profile")
    public ApiResponse<UserDto> updateProfile(
            @Valid @RequestBody UserDto userDto
    ) {
        return userService.updateProfile(userDto);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProfile(@PathVariable UUID id) {
        return userService.deleteProfile(id);
    }
}