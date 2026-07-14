package com.library.bff.dto;

public record SessionResponse(
        String email,
        boolean authenticated
) {
}
