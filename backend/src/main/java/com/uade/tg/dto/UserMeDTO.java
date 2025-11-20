package com.uade.tg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserMeDTO {
    private Long id;
    private String userName;
    private String email;
}