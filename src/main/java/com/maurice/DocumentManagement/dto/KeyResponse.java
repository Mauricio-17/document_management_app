package com.maurice.DocumentManagement.dto;

import java.util.Set;

public record KeyResponse(
        Set<String> objectKeys,
        Set<String> folderNames
) {
}
