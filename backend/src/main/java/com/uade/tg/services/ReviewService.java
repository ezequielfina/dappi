package com.uade.tg.services;

import com.uade.tg.dto.CreateReviewRequestDTO;
import com.uade.tg.entities.Place;
import com.uade.tg.entities.Review;
import com.uade.tg.entities.ReviewVote;
import com.uade.tg.entities.User;
import com.uade.tg.repositories.PlaceRepository;
import com.uade.tg.repositories.ReviewRepository;
import com.uade.tg.repositories.ReviewVoteRepository;
import com.uade.tg.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public Review createReview(CreateReviewRequestDTO req) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Place place = placeRepository.findById(req.getPlaceId())
                .orElseThrow(() -> new EntityNotFoundException("Lugar no encontrado"));


        //TODO: Agregar la foto que se sube cuando hacemos la review
        Review review = Review.builder()
                .user(user)
                .place(place)
                .description(req.getDescription())
                .rateToPlace(req.getRateToPlace())
                .reviewVotes(0)
                .build();

        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByPlace(Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new EntityNotFoundException("Lugar no encontrado"));

        return reviewRepository.findByPlace(place);
    }

    public List<Review> getReviewsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        return reviewRepository.findAllReviewsByUser(user.getId());
    }

    public Review upVoteReview(Long userId,Long reviewId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review no encontrada"));

        boolean alreadyVoted = reviewVoteRepository.existsByUserIdAndReviewId(currentUser.getId(), reviewId);
        if (alreadyVoted) {
            throw new IllegalStateException("Ya votaste esta review");
        }

        ReviewVote vote = new ReviewVote();
        vote.setUser(currentUser);
        vote.setReview(review);
        vote.setValue(1);
        reviewVoteRepository.save(vote);

        // actualizar contador en la review
        review.setReviewVotes(review.getReviewVotes() + 1);
        return reviewRepository.save(review);
    }

    public Review downVoteReview(Long userId, Long reviewId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review no encontrada"));

        boolean alreadyVoted = reviewVoteRepository.existsByUserIdAndReviewId(currentUser.getId(), reviewId);
        if (alreadyVoted) {
            throw new IllegalStateException("Ya votaste esta review");
        }

        ReviewVote vote = new ReviewVote();
        vote.setUser(currentUser);
        vote.setReview(review);
        vote.setValue(-1);
        reviewVoteRepository.save(vote);

        review.setReviewVotes(review.getReviewVotes() - 1);
        return reviewRepository.save(review);
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