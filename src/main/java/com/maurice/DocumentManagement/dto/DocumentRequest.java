package com.maurice.DocumentManagement.dto;

public record DocumentRequest(
        // The key column is the result of the provided folder key + filename + filetype
        String fileName,
        String fileType,
        String status,
        Boolean isPublic,
        long size,
        String key,
        Long folderId,
        Long userId
) {
}
