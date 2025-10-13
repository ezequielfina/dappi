package com.uade.tg.services;

import com.uade.tg.entities.Health;
import com.uade.tg.exceptions.BusinessException;
import com.uade.tg.repositories.HealthRep;
import lombok.RequiredArgsConstructor;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HealthService {
    private final HealthRep healthRep;

    public Health getStatus() {
        try {
            Health health = Health.builder()
                    .status("OK")
                    .build();

            return this.healthRep.save(health);
        } catch (DataAccessException ex) {
            throw new BusinessException("connection to the db failed");
        }
    }

}
