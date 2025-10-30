package com.uade.tg.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Table(name = "health")
public class Health {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "status")
    private String status;
}
