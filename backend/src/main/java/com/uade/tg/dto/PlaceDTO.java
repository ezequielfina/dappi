package com.uade.tg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceDTO {
    private Long id;
    private String name;
    private String full_address;
    private String url;
    private String description;
    private String placeCategory;
}
