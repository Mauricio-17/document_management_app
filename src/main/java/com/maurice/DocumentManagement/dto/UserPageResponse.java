package com.maurice.DocumentManagement.dto;

import java.util.List;

public record UserPageResponse(
        List<UserResponse> content,
        int pageNo,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean last
) {

}

