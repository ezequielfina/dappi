package com.uade.tg.controllers;

import com.uade.tg.controllers.utils.PlaceTypeTransformer;
import com.uade.tg.dto.PlaceTypeDTO;
import com.uade.tg.services.PlaceTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/place_type")
@RequiredArgsConstructor
public class PlaceTypeController {
    private final PlaceTypeService placeTypeService;

    @PostMapping
    public ResponseEntity<PlaceTypeDTO> saveNewPlaceType(@RequestBody PlaceTypeDTO placeTypeDTO) {
        PlaceType placeType = PlaceTypeTransformer.transformToEntity(placeTypeDTO);
        placeType = this.placeTypeService.addNewPlaceType(placeType);

        placeTypeDTO = PlaceTypeTransformer.transformToDTO(placeType);
        return ResponseEntity.status(HttpStatus.CREATED).body(placeTypeDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaceTypeDTO> getPlaceTypeById(@PathVariable Long id) {
        PlaceType placeType = this.placeTypeService.findPlaceTypeById(id);

        PlaceTypeDTO placeTypeDTO = PlaceTypeTransformer.transformToDTO(placeType);
        return ResponseEntity.ok().body(placeTypeDTO);
    }

    @GetMapping
    public ResponseEntity<List<PlaceTypeDTO>> getAllPlaceType() {
        List<PlaceType> placeTypeList = this.placeTypeService.findAllPlaceTypes();

        List<PlaceTypeDTO> placeTypeDTOList = PlaceTypeTransformer.transformListToDTOList(placeTypeList);
        return ResponseEntity.ok().body(placeTypeDTOList);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PlaceTypeDTO> updateDescription(@PathVariable Long id, @RequestBody PlaceTypeDTO placeTypeDTO) {
        PlaceType placeType = this.placeTypeService.updateDescription(id, placeTypeDTO.description());

        placeTypeDTO = PlaceTypeTransformer.transformToDTO(placeType);
        return ResponseEntity.ok().body(placeTypeDTO);
    }
}
