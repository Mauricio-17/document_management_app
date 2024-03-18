package com.maurice.DocumentManagement.dto;

import lombok.*;


@Builder
public record AuthenticationResponse(
        String accessToken,
        String refreshToken
) {
}
