package com.library.users.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;

import com.library.users.dto.ApiResponse;
import com.library.users.dto.UserDto;
import com.library.users.service.UserService;

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
    public ApiResponse<UserDto> updateProfile(@RequestBody UserDto userDto) {
        return userService.updateProfile(userDto);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProfile(@PathVariable UUID id) {
        return userService.deleteProfile(id);
    }
}