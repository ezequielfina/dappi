package com.uade.tg.controllers;

import com.uade.tg.dto.PlaceDTO;
import com.uade.tg.entities.Place;
import com.uade.tg.services.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping
    public List<PlaceDTO> getAllPlaces() {
        return placeService.getPlaces();
    }

    @GetMapping("/category/{category}")
    public List<Place> getPlacesByCategory(@PathVariable String category) {
        return placeService.getPlacesByCategory(category);
    }
}
