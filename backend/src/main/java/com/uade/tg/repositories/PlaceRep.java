package com.uade.tg.repositories;

import com.uade.tg.entities.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRep extends JpaRepository<Place, Long> {
    List<Place> findAllByPlaceCategory(String placeCategory);
}