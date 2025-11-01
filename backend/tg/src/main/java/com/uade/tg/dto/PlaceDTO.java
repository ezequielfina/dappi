package com.uade.tg.dto;

import lombok.Builder;

import java.sql.Time;

@Builder
public record PlaceDTO(
    Long id,
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
