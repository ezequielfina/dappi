package com.uade.tg.repositories;

import com.uade.tg.entities.Health;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthRep extends JpaRepository<Health, Long> {

}
