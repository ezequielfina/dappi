package com.uade.tg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserMeDTO {
    private Long id;
    private String userName;
    private String email;
    private int resenasRealizadas;
    private int upvotes;
    private ReviewResponseDTO reviewMasVotada;

}