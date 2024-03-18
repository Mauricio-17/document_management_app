package com.maurice.DocumentManagement.dto;

import java.util.List;

public record DocumentPageResponse(
        List<DocumentResponse> content,
        int pageNo,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean last
) {
}
