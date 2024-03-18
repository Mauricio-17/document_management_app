package com.maurice.DocumentManagement.dto;

import lombok.Data;

@Data
public class FolderResponse {
    private Long id;
    private String name;
    private String description;
    private String key;
    private String userName;
    private String createdAt;
    private String lastModifiedAt;

    public FolderResponse(Long id, String name, String description, String key, String createdAt, String lastModifiedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.key = key;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
    }
}
