package com.uade.tg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateReviewRequestDTO {
    private Long userId;
    private Long placeId;
    private String description;
    private Integer rateToPlace;
    private LocalDateTime createdAt;
    private String photoUrl;
}
