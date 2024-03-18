package com.maurice.DocumentManagement.dto;

import lombok.Data;

public record PlanDto(
        Long id,
        String name,
        String description,
        Float monthlyCost
) {
}
