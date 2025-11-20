package com.uade.tg.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceTypeRep extends JpaRepository<PlaceType, Long> {
    boolean existsPlaceTypeById(Long id);
}
