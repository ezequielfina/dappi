package com.uade.tg.services;

import com.uade.tg.entities.PlaceType;
import com.uade.tg.exceptions.BusinessException;
import com.uade.tg.exceptions.NotFoundException;
import com.uade.tg.repositories.PlaceTypeRep;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlaceTypeService {
    private final PlaceTypeRep placeTypeRep;

    public PlaceType addNewPlaceType(PlaceType placeType) {
        try {
            if(placeType.getId() == null || !this.placeTypeExistsById(placeType)) {
                return this.placeTypeRep.save(placeType);
            }
            throw new BusinessException("The place type already exists. It can't be replaced, must use the update endpoint.");
        } catch (DataAccessException ex) {
            throw new BusinessException("connection to the db failed");
        }
    }

    private boolean placeTypeExistsById(PlaceType placeType) {
        return this.placeTypeRep.existsPlaceTypeById(placeType.getId());
    }

    public PlaceType findPlaceTypeById(UUID id) {
        try {
            return this.placeTypeRep.findById(id).orElseThrow(
                    () -> new NotFoundException("placeType")
            );
        } catch (DataAccessException ex) {
            throw new BusinessException("connection to the db failed");
        }
    }

    public List<PlaceType> findAllPlaceTypes() {
        try {
            return this.placeTypeRep.findAll();
        } catch (DataAccessException ex) {
            throw new BusinessException("connection to the db failed");
        }
    }

    public PlaceType updateDescription(UUID id, String newDescription) {
        try {
            PlaceType placeType = this.findPlaceTypeById(id);

            placeType.setDescription(newDescription);
            return this.placeTypeRep.save(placeType);
        } catch (DataAccessException ex) {
            throw new BusinessException("connection to the db failed");
        }
    }
}
