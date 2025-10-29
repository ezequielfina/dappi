package com.uade.tg.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Table(name = "place_types")
public class PlaceType {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "placeType", cascade = CascadeType.ALL)
    private List<Place> places;

}
