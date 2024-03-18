package com.maurice.DocumentManagement.dto;

import lombok.Data;

@Data
public class ShareResponse{
    private Long id;
    private String permission;
    private String documentName;
    private String folderName;
    private String createdAt;
    private String lastModifiedAt;

    public ShareResponse(Long id, String permission, String createdAt, String lastModifiedAt) {
        this.id = id;
        this.permission = permission;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
    }
}

