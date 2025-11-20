package com.uade.tg.repositories;

import com.uade.tg.entities.Review;
import com.uade.tg.entities.Place;
import com.uade.tg.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.place.id = :placeId ORDER BY r.reviewVotes DESC")
    List<Review> findByPlaceOrdered(@Param("placeId") Long placeId);

    @Query("SELECT r FROM Review r WHERE r.user.id = :userId ORDER BY r.reviewVotes DESC")
    List<Review> findAllReviewsByUser(@Param("userId") Long userId);

    Review findFirstByUserIdOrderByReviewVotesDesc(Long userId);

}
