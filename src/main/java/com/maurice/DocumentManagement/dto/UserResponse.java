package com.maurice.DocumentManagement.dto;

import lombok.Data;

@Data
public class UserResponse {

    private Long id;
    private String name;
    private String lastname;
    private String email;
    private String planName;
    private String createdAt;
    private String lastModifiedAt;

    public UserResponse(Long id, String name, String lastname, String email, String createdAt, String lastModifiedAt) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
    }
}
