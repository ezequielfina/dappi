package com.uade.tg.controllers;

import com.uade.tg.controllers.utils.HealthTransformer;
import com.uade.tg.dto.HealthDTO;
import com.uade.tg.entities.Health;
import com.uade.tg.services.HealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthController {
    private final HealthService healthService;

    @GetMapping("/entity")
    public ResponseEntity<HealthDTO> getHealthEntityExample() {
        Health health = this.healthService.getStatus();
        HealthDTO healthDTO = HealthTransformer.transformEntityToDto(health);

        return ResponseEntity.ok().body(healthDTO);
    }

    @GetMapping
    public ResponseEntity<String> getHealthStatus() {
        Health health = this.healthService.getStatus();
        HealthDTO healthDTO = HealthTransformer.transformEntityToDto(health);

        return ResponseEntity.ok().body(healthDTO.status());
    }
}
