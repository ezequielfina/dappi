package com.uade.tg.controllers.utils;

import com.uade.tg.dto.HealthDTO;
import com.uade.tg.entities.Health;

public abstract class HealthTransformer {
    public static HealthDTO transformEntityToDto(Health health) {
        return HealthDTO.builder()
                .id(health.getId())
                .status(health.getStatus())
                .build();
    }

    public static Health transformDtoToEntity(HealthDTO healthDTO) {
        return Health.builder()
                .id(healthDTO.id())
                .status(healthDTO.status())
                .build();
    }
}
