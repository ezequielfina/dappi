package com.uade.tg.controllers.utils;

import com.uade.tg.dto.PlaceTypeDTO;
import com.uade.tg.entities.PlaceType;

import java.util.ArrayList;
import java.util.List;

public abstract class PlaceTypeTransformer {
    public static PlaceType transformToEntity(PlaceTypeDTO placeTypeDTO) {
        return PlaceType.builder()
                .id(placeTypeDTO.id())
                .description(placeTypeDTO.description())
                .build();
    }

    public static PlaceTypeDTO transformToDTO(PlaceType placeType) {
        return PlaceTypeDTO.builder()
                .id(placeType.getId())
                .description(placeType.getDescription())
                .build();
    }

    public static List<PlaceTypeDTO> transformListToDTOList(List<PlaceType> placeTypes) {
        List<PlaceTypeDTO> placeTypeDTOList = new ArrayList<>();
        for(PlaceType placeType : placeTypes) {
            placeTypeDTOList.add(transformToDTO(placeType));
        }

        return placeTypeDTOList;
    }
}
