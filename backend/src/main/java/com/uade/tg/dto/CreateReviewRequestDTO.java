package com.uade.tg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateReviewRequestDTO {
    private Long userId;
    private Long placeId;
    private String description;
    private Integer rateToPlace;
}
