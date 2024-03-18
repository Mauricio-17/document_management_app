package com.maurice.DocumentManagement.dto;

public record FolderRequest(
        // The key is composed of the current key plus the name
        String name,
        String description,
        String key,
        Long userId
) {
}
