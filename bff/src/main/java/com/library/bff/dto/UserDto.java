package com.library.bff.dto;

import java.util.UUID;

public record UserDto(
        UUID id,
        String authEmail,
        String fullName,
        String phone,
        String address
) {

}
