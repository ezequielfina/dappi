package com.uade.tg.dto;

import lombok.Builder;

@Builder
public record PlaceTypeDTO(
    Long id,
    String description
) {}
