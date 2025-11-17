package com.uade.tg.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uade.tg.dto.CreateReviewRequestDTO;
import com.uade.tg.dto.ReviewResponseDTO;
import com.uade.tg.entities.Review;
import com.uade.tg.entities.User;
import com.uade.tg.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    
    @PostMapping(value = "/with-photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponseDTO> createWithPhotos(
            @RequestPart("review") String reviewJson,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {
        try {
            // Parsear el JSON de la reseña
            ObjectMapper objectMapper = new ObjectMapper();
            CreateReviewRequestDTO req = objectMapper.readValue(reviewJson, CreateReviewRequestDTO.class);
            
            // Crear la reseña con fotos
            ReviewResponseDTO response = reviewService.createReviewWithPhotos(req, photos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar la solicitud: " + e.getMessage());
        }
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
