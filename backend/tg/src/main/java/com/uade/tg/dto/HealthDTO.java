package com.uade.tg.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record HealthDTO(
        UUID id,
        String status
) {
}
