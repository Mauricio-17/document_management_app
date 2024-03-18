package com.maurice.DocumentManagement.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

public record DocumentDetailRequest(
     Long id,
     Long documentId,
     Long folderId,
     Long shareId
) {
}
