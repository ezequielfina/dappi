package com.uade.tg.services;

import com.uade.tg.entities.Place;
import com.uade.tg.exceptions.BusinessException;
import com.uade.tg.exceptions.NotFoundException;
import com.uade.tg.repositories.PlaceRep;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRep placeRep;

    public Place addNewPlace(Place place) {
        try {
            if(place.getId() == null || !this.placeExistsById(place)) {

                return this.placeRep.save(place);
            } throw new BusinessException("The place already exists. It can't be replaced, must use the update endpoint.");
        } catch (DataAccessException ex) {
            System.out.println(ex.getMessage());
            throw new BusinessException("connection to the db failed");
        }
    }

    private boolean placeExistsById(Place place) {
        return this.placeRep.existsById(place.getId());
    }

    public Place findPlaceById(Long id) {
        try {
            return this.placeRep.findById(id).orElseThrow(
                    () -> new NotFoundException("place")
            );
        } catch (DataAccessException ex) {
            throw new BusinessException("connection to the db failed");
        }
    }

    public List<Place> findAllPlaces() {
        try {
            return this.placeRep.findAll();
        } catch (DataAccessException ex) {
            throw new BusinessException("connection to the db failed");
        }
    }

    public List<Place> findAllPlacesByPlaceTypeId(String placeCategory) {
        try {
            return this.placeRep.findAllByPlaceCategory(placeCategory);
        } catch (DataAccessException ex) {
            throw new BusinessException("connection to the db failed");
        }
    }
}