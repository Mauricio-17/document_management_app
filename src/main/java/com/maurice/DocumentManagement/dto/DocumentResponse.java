package com.maurice.DocumentManagement.dto;

import lombok.Data;

@Data
public class DocumentResponse{

    private Long id;
    private String fileName;
    private String fileType;
    private Long size;
    private String status;
    private Boolean isPublic;
    private String key;
    private String folderName;
    private String createdAt;
    private String lastModifiedAt;


    public DocumentResponse(Long id, String fileName, String fileType, Long size, String status, Boolean isPublic, String key, String createdAt, String lastModifiedAt) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.size = size;
        this.status = status;
        this.isPublic = isPublic;
        this.key = key;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
    }
}
