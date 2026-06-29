package com.library.users.dto;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserDto(
    UUID id,
    @NotBlank @Email String authEmail,
    @NotBlank String fullName,
    @NotBlank String phone,
    @NotBlank String address
) {

}
