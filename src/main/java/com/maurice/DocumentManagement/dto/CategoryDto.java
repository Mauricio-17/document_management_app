package com.maurice.DocumentManagement.dto;

public record CategoryDto(
        Long id,
        String name,
        String description,
        String createdAt,
        String lastModifiedAt
) {
}
