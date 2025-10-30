package com.uade.tg.dto;

import lombok.Builder;

@Builder
public record HealthDTO(
        Long id,
        String status
) {
}
