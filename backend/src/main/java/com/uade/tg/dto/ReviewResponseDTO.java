package com.uade.tg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ReviewResponseDTO {
    private Long id;
    private String description;
    private Integer rateToPlace;
    private Integer reviewVotes;
    private Long userId;
    private Long placeId;
    private LocalDateTime createdAt;
    private String photoUrl;
}
