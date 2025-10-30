package com.uade.tg.repositories;

import com.uade.tg.entities.PlaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlaceTypeRep extends JpaRepository<PlaceType, UUID> {
    boolean existsPlaceTypeById(UUID id);
}
