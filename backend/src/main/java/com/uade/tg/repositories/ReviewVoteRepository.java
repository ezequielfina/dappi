package com.uade.tg.repositories;

import com.uade.tg.entities.ReviewVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewVoteRepository extends JpaRepository<ReviewVote, Long> {

    Optional<ReviewVote> findByUserIdAndReviewId(Long userId, Long reviewId);

    boolean existsByUserIdAndReviewId(Long userId, Long reviewId);
}
