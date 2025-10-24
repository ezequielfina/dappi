package com.uade.tg.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String userName;
    private String email;
    private String phoneNumber;
    private String password;
    private Review review;

}
