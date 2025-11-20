package com.uade.tg.controllers.utils;

import com.uade.tg.dto.PlaceDTO;
import com.uade.tg.dto.PlaceTypeDTO;
import com.uade.tg.entities.Place;

import java.util.ArrayList;
import java.util.List;

public abstract class PlaceTransformer {
    public static PlaceDTO transformToDTO(Place place) {
        return PlaceDTO.builder()
                .id(place.getId())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .name(place.getName())
                .placeType(PlaceTypeTransformer.transformToDTO(place.getPlaceType()))
                .full_address(place.getFull_address())
                .description(place.getDescription())
                .entryTime(place.getEntryTime())
                .endTime(place.getEndTime())
                .instagram(place.getInstagram())
                .website(place.getWebsite())
                .build();
    }

    public static Place transformToEntity(PlaceDTO placeDTO) {
        return Place.builder()
                .id(placeDTO.id())
                .latitude(placeDTO.latitude())
                .longitude(placeDTO.longitude())
                .name(placeDTO.name())
                .placeType(PlaceTypeTransformer.transformToEntity(placeDTO.placeType()))
                .full_address(placeDTO.full_address())
                .description(placeDTO.description())
                .entryTime(placeDTO.entryTime())
                .endTime(placeDTO.endTime())
                .instagram(placeDTO.instagram())
                .website(placeDTO.website())
                .build();
    }

    public static List<PlaceDTO> transformListToDTOList(List<Place> placeList) {
        List<PlaceDTO> placeDTOList = new ArrayList<>();
        for(Place place : placeList) {
            placeDTOList.add(transformToDTO(place));
        }

        return placeDTOList;
    }
}
