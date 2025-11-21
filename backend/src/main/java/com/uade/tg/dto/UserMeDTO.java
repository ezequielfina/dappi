package com.uade.tg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserMeDTO {
    private Long id;
    private String userName;
    private String email;
    private String country;
    private String favoritePlace;
    
    @JsonProperty("profilePicture")
    private String profilePicture;
    
    private int resenasRealizadas;
    private int upvotes;
    private ReviewResponseDTO reviewMasVotada;

}