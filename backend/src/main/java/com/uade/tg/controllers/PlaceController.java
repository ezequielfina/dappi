package com.uade.tg.controllers;

import com.uade.tg.controllers.utils.PlaceTransformer;
import com.uade.tg.dto.PlaceDTO;
import com.uade.tg.entities.Place;
import com.uade.tg.services.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(("/place"))
@RequiredArgsConstructor
public class PlaceController {
    private final PlaceService placeService;

    @PostMapping
    public ResponseEntity<PlaceDTO> saveNewPlace(@RequestBody PlaceDTO placeDTO) {
        Place place = PlaceTransformer.transformToEntity(placeDTO);
        place = this.placeService.addNewPlace(place);

        placeDTO = PlaceTransformer.transformToDTO(place);
        return ResponseEntity.status(HttpStatus.CREATED).body(placeDTO);
    }

    @GetMapping
    public ResponseEntity<List<PlaceDTO>> getAllPlaces() {
        List<Place> places = this.placeService.findAllPlaces();

        List<PlaceDTO> placeDTOList = PlaceTransformer.transformListToDTOList(places);
        return ResponseEntity.ok().body(placeDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaceDTO> getPlaceById(@PathVariable Long id) {
        Place place = this.placeService.findPlaceById(id);

        PlaceDTO placeDTO = PlaceTransformer.transformToDTO(place);
        return ResponseEntity.ok().body(placeDTO);
    }

    @GetMapping("/byPlaceType")
    public ResponseEntity<List<PlaceDTO>> getAllPlaces(@RequestParam String placeCategory) {
        List<Place> places = this.placeService.findAllPlacesByPlaceTypeId(placeCategory);

        List<PlaceDTO> placeDTOList = PlaceTransformer.transformListToDTOList(places);
        return ResponseEntity.ok().body(placeDTOList);
    }
}