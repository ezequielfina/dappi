package com.uade.tg.services;

import com.uade.tg.dto.PlaceDTO;
import com.uade.tg.entities.Place;
import com.uade.tg.repositories.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    public List<PlaceDTO> getPlaces() {
        return placeRepository.findAll().stream()
                .map(place -> {
                    PlaceDTO dto = new PlaceDTO();
                    dto.setId(place.getId());
                    dto.setName(place.getName());
                    dto.setFull_address(place.getFull_address());
                    dto.setUrl(place.getUrl());
                    dto.setDescription(place.getDescription());
                    dto.setPlaceCategory(place.getPlaceCategory());
                    return dto;
                })
                .toList();
    }


    public List<Place> getPlacesByCategory(String category) {
        return placeRepository.findByPlaceCategoryIgnoreCase(category);
    }
}
