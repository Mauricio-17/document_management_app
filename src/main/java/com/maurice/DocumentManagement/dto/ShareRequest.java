package com.maurice.DocumentManagement.dto;

import java.util.List;

public record ShareRequest(
        String permission,
        List<String> documentKeys,
        List<String> userEmails
) {
}
