package com.uade.tg.dto;

import com.uade.tg.enums.PlaceCategories;
import lombok.Builder;

import java.sql.Time;

@Builder
public record PlaceDTO(
        Long id,
        Double latitude,
        Double longitude,
        String name,
        PlaceCategories placeType,
        String full_address,
        String description,
        Time entryTime,
        Time endTime,
        String instagram,
        String website
) {
}