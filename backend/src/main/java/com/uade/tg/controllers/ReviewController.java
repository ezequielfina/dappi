package com.uade.tg.controllers;

import com.uade.tg.dto.CreateReviewRequestDTO;
import com.uade.tg.entities.Review;
import com.uade.tg.entities.User;
import com.uade.tg.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {


    @Autowired
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> create(@RequestBody CreateReviewRequestDTO req) {
        return ResponseEntity.ok(reviewService.createReview(req));
    }

    @GetMapping("/places/{placeId}")
    public ResponseEntity<List<Review>> getReviewsByPlace(@PathVariable Long placeId) {
        return ResponseEntity.ok(reviewService.getReviewsByPlace(placeId));
    }

    @GetMapping("/users")
    public ResponseEntity<List<Review>> getReviewsByUser(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(user.getId()));
    }

    @PostMapping("/{reviewId}/votes/up")
    public ResponseEntity<Review> upVote(
            @AuthenticationPrincipal User user,
            @PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.upVoteReview(user.getId(),reviewId));
    }
    @PostMapping("/{reviewId}/votes/down")
    public ResponseEntity<Review> downVote(
            @AuthenticationPrincipal User user,
            @PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.downVoteReview(user.getId(),reviewId));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(
            @AuthenticationPrincipal User user,
            @PathVariable Long reviewId) {
        reviewService.deleteReview(user.getId(), reviewId);
        return ResponseEntity.noContent().build();
    }

}
