package com.library.returns.client.dto;

import java.util.UUID;

public record FineClientRequest(
        UUID userId,
        UUID loanId,
        int delayedDays
) {
}