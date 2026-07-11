package com.library.loans.client.dto;

import java.util.UUID;

public record UserClientResponse(
        UUID id,
        String authEmail,
        String fullName,
        String phone,
        String address
) {
}
