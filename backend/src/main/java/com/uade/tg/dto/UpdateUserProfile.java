package com.uade.tg.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserProfile {
    private String email;
    private String userName;
    private String password;
    private String profilePicture;
}