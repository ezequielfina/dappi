package com.uade.tg.controllers;


import com.uade.tg.dto.FirstRegisterDTO;
import com.uade.tg.dto.UpdateUserProfileDTO;
import com.uade.tg.entities.Review;
import com.uade.tg.entities.User;
import com.uade.tg.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PutMapping("/me/update")
    public ResponseEntity<Optional<User>> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody UpdateUserProfileDTO request) {
        return ResponseEntity.ok(userService.updateProfile(user.getId(), request));
    }

    @PutMapping("/onboarding")
    public ResponseEntity<Optional<User>> onBoarding(
            @AuthenticationPrincipal User user,
            @RequestBody FirstRegisterDTO request) {
        return ResponseEntity.ok(userService.onBoardingPage(user.getId(),request));
    }


}
