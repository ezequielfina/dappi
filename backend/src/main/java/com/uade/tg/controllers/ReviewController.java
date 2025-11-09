package com.uade.tg.controllers;

import com.uade.tg.dto.CreateReviewRequestDTO;
import com.uade.tg.dto.ReviewResponseDTO;
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

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> create(@RequestBody CreateReviewRequestDTO req,
                                                    @AuthenticationPrincipal User user) {
        Review review = reviewService.createReview(req, user);
        ReviewResponseDTO response = new ReviewResponseDTO(
                review.getId(),
                review.getDescription(),
                review.getRateToPlace(),
                review.getReviewVotes(),
                review.getUser().getId(),
                review.getPlace().getId(),
                review.getPhotoUrl()
        );
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/places/{placeId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByPlace(@PathVariable Long placeId) {
        List<ReviewResponseDTO> reviews = reviewService.getReviewsByPlace(placeId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/users")
    public ResponseEntity<List<ReviewResponseDTO>>  getReviewsByUser(
            @AuthenticationPrincipal User user) {

        List<ReviewResponseDTO> reviews = reviewService.getReviewsByUser(user.getId());
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/{reviewId}/votes/up")
    public ResponseEntity<ReviewResponseDTO> upVote(
            @AuthenticationPrincipal User user,
            @PathVariable Long reviewId) {

        ReviewResponseDTO updatedReview = reviewService.upVoteReview(user.getId(), reviewId);
        return ResponseEntity.ok(updatedReview);
    }


    @PostMapping("/{reviewId}/votes/down")
    public ResponseEntity<ReviewResponseDTO> downVote(
            @AuthenticationPrincipal User user,
            @PathVariable Long reviewId) {

        ReviewResponseDTO updatedReview = reviewService.downVoteReview(user.getId(), reviewId);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(
            @AuthenticationPrincipal User user,
            @PathVariable Long reviewId) {
        reviewService.deleteReview(user.getId(), reviewId);
        return ResponseEntity.noContent().build();
    }

}
