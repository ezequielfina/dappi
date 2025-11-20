package com.uade.tg.services;

import com.uade.tg.dto.CreateReviewRequestDTO;
import com.uade.tg.dto.ReviewResponseDTO;
import com.uade.tg.entities.Place;
import com.uade.tg.entities.Review;
import com.uade.tg.entities.ReviewVote;
import com.uade.tg.entities.User;
import com.uade.tg.enums.VoteType;
import com.uade.tg.repositories.PlaceRepository;
import com.uade.tg.repositories.ReviewRepository;
import com.uade.tg.repositories.ReviewVoteRepository;
import com.uade.tg.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewVoteRepository reviewVoteRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository, PlaceRepository placeRepository, ReviewVoteRepository reviewVoteRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.placeRepository = placeRepository;
        this.reviewVoteRepository = reviewVoteRepository;
    }

    public Integer getSumUpvotesByUserId(Long userId) {
        return this.reviewRepository.sumReviewVotesByUserId(userId);
    }

    public ReviewResponseDTO getReviewMostUpByUser(Long userId) {
        Review review = reviewRepository.findFirstByUserIdOrderByReviewVotesDesc(userId);
        if (review == null) {
            // Retorna NULL si no hay reseña, lo que indica que el usuario no tiene ninguna.
            return null;
        }

        return ReviewResponseDTO.builder()
                .id(review.getId())
                .description(review.getDescription())
                .rateToPlace(review.getRateToPlace())
                .reviewVotes(review.getReviewVotes())
                .build();
    }

    public Review createReview(CreateReviewRequestDTO req, User user) {
        Place place = placeRepository.findById(req.getPlaceId())
                .orElseThrow(() -> new RuntimeException("Place not found"));

        Review review = new Review();
        review.setUser(user);
        review.setPlace(place);
        review.setDescription(req.getDescription());
        review.setRateToPlace(req.getRateToPlace());
        review.setPhotoUrl(req.getPhotoUrl());

        return reviewRepository.save(review);
    }

    public List<ReviewResponseDTO> getReviewsByPlace(Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new EntityNotFoundException("Lugar no encontrado"));
        List<Review> reviews = reviewRepository.findByPlaceOrdered(placeId);

        return reviews.stream()
                .map(r -> new ReviewResponseDTO(
                        r.getId(),
                        r.getDescription(),
                        r.getRateToPlace(),
                        r.getReviewVotes(),
                        r.getUser().getId(),
                        r.getPlace().getId(),
                        r.getPhotoUrl()
                ))
                .toList();
    }


    public List<ReviewResponseDTO> getReviewsByUser(Long userId) {
        List<Review> reviews = reviewRepository.findAllReviewsByUser(userId);
        return reviews.stream()
                .map(r -> new ReviewResponseDTO(
                        r.getId(),
                        r.getDescription(),
                        r.getRateToPlace(),
                        r.getReviewVotes(),
                        r.getUser().getId(),
                        r.getPlace().getId(),
                        r.getPhotoUrl()
                ))
                .toList();
    }
    public ReviewResponseDTO getTopReviewByPlace(Long placeId) {
        List<Review> reviews = reviewRepository.findByPlaceOrdered(placeId);

        if (reviews.isEmpty()) {
            return null;
        }

        Review topReview = reviews.get(0);

        return ReviewResponseDTO.builder()
                .id(topReview.getId())
                .description(topReview.getDescription())
                .rateToPlace(topReview.getRateToPlace())
                .reviewVotes(topReview.getReviewVotes())
                .userId(topReview.getUser().getId())
                .placeId(topReview.getPlace().getId())
                .photoUrl(topReview.getPhotoUrl())
                .build();
    }
    public ReviewResponseDTO upVoteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review no encontrada"));

        Optional<ReviewVote> existingVote = reviewVoteRepository.findByUserIdAndReviewId(userId, reviewId);

        if (existingVote.isPresent()) {
            ReviewVote vote = existingVote.get();
            if (vote.getVoteType() == VoteType.UPVOTE) {
                throw new IllegalStateException("Ya diste upvote a esta review");
            } else {
                // Cambiar downvote -> upvote
                vote.setVoteType(VoteType.UPVOTE);
                reviewVoteRepository.save(vote);
                review.setReviewVotes(review.getReviewVotes() + 2); // de -1 a +1
            }
        } else {
            // Crear nuevo upvote
            ReviewVote newVote = new ReviewVote();
            newVote.setUserId(userId);
            newVote.setReviewId(reviewId);
            newVote.setVoteType(VoteType.UPVOTE);
            reviewVoteRepository.save(newVote);

            review.setReviewVotes(review.getReviewVotes() + 1);
        }

        reviewRepository.save(review);
        return new ReviewResponseDTO(
                review.getId(),
                review.getDescription(),
                review.getRateToPlace(),
                review.getReviewVotes(),
                review.getUser().getId(),
                review.getPlace().getId(),
                review.getPhotoUrl()
        );
    }
    public ReviewResponseDTO downVoteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review no encontrada"));

        Optional<ReviewVote> existingVote = reviewVoteRepository.findByUserIdAndReviewId(userId, reviewId);

        if (existingVote.isPresent()) {
            ReviewVote vote = existingVote.get();
            if (vote.getVoteType() == VoteType.DOWNVOTE) {
                throw new IllegalStateException("Ya diste downvote a esta review");
            } else {
                vote.setVoteType(VoteType.DOWNVOTE);
                reviewVoteRepository.save(vote);
                review.setReviewVotes(review.getReviewVotes() - 2);
            }
        } else {
            // Crear nuevo downvote
            ReviewVote newVote = new ReviewVote();
            newVote.setUserId(userId);
            newVote.setReviewId(reviewId);
            newVote.setVoteType(VoteType.DOWNVOTE);
            reviewVoteRepository.save(newVote);

            review.setReviewVotes(review.getReviewVotes() - 1);
        }

        reviewRepository.save(review);
        return new ReviewResponseDTO(
                review.getId(),
                review.getDescription(),
                review.getRateToPlace(),
                review.getReviewVotes(),
                review.getUser().getId(),
                review.getPlace().getId(),
                review.getPhotoUrl()
        );
    }



    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review no encontrada"));

        if (!review.getUser().getId().equals(userId)) {
            throw new SecurityException("No podés borrar una review de otro usuario");
        }

        reviewRepository.delete(review);
    }


}