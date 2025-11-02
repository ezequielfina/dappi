package com.uade.tg.repositories;

import com.uade.tg.entities.Review;
import com.uade.tg.entities.Place;
import com.uade.tg.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByPlace(Long placeId);

    @Query("SELECT r FROM Review r WHERE r.user.id = :userId ORDER BY r.reviewVotes DESC")
    List<Review> findAllReviewsByUser(Long userId);

}
