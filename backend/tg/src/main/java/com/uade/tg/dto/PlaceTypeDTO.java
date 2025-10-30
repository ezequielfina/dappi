package com.uade.tg.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PlaceTypeDTO(
    UUID id,
    String description
) {}
