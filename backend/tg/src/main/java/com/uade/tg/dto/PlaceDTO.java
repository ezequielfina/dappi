package com.uade.tg.dto;

import lombok.Builder;

import java.sql.Time;
import java.util.UUID;

@Builder
public record PlaceDTO(
    UUID id,
    Double latitude,
    Double longitude,
    String name,
    PlaceTypeDTO placeType,
    String full_address,
    String description,
    Time entryTime,
    Time endTime,
    String instagram,
    String website
) {
}
