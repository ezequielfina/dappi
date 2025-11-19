package com.uade.tg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDTO {
    private Long id;
    private String description;
    private Integer rateToPlace;
    private Integer reviewVotes;
    private Long userId;
    private String userName;
    private Long placeId;
    private String placeName;
    private List<ReviewPhotoDTO> photos;
    private Double userLatitude;
    private Double userLongitude;
}

