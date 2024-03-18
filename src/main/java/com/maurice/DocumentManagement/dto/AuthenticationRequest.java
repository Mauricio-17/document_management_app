package com.maurice.DocumentManagement.dto;

import lombok.Builder;

@Builder
public record AuthenticationRequest(
        String email,
        String password
) {
}
