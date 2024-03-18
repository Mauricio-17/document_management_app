package com.maurice.DocumentManagement.dto;


public record UserRequest(
        String name,
        String lastname,
        String email,
        String password,
        Long planId

) {
}
