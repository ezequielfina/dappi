package com.uade.tg.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Time;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Table(name = "places")
public class Place {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "name")
    private String name;

    @Column(name = "full_address")
    private String full_address;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "entry_time")
    private Time entryTime;

    @Column(name = "end_time")
    private Time endTime;

    @Column(name = "ig")
    private String instagram;

    @Column(name = "website")
    private String website;

    @Column(name = "photo", length = 2000)
    private String url;

    @Column(name = "place_category")
    private String placeCategory;

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

}

