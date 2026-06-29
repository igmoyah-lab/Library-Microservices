package com.library.bff.dto;

public record ProfileUpdateRequest(
        String fullName,
        String phone,
        String address
) {

}
