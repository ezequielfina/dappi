package com.uade.tg.repositories;

import com.uade.tg.entities.Place;
import com.uade.tg.entities.PlaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlaceRep extends JpaRepository<Place, UUID> {
    List<Place> findAllByPlaceType_Id(UUID placeTypeId);
}
