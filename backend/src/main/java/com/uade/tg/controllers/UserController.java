package com.uade.tg.controllers;


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

    @GetMapping("/me/reviews")
    public ResponseEntity<List<Review>> getMyReviews(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(user.getReviews());
    }

    @PutMapping("/me/update")
    public ResponseEntity<Optional<User>> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody UpdateUserProfileDTO request) {
        return ResponseEntity.ok(userService.updateProfile(user.getId(), request));
    }

    @GetMapping("/me/reviews/top")
    public ResponseEntity<List<Review>> getMyTopReviews(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getMyReviewsOrderedByVotes(user.getId()));
    }


}
