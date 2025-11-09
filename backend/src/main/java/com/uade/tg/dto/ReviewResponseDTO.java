package com.uade.tg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDTO {
    private Long id;
    private String description;
    private Integer rateToPlace;
    private Integer reviewVotes;
    private Long userId;
    private Long placeId;
    private String photoUrl;
}
