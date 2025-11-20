package data.database.dao;

import androidx.room.*;
import java.util.List;
import data.database.entity.ReviewEntity;

@Dao
public interface ReviewDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ReviewEntity review);

    @Update
    void update(ReviewEntity review);

    @Query("SELECT * FROM reviews WHERE placeId = :placeId ORDER BY createdAt DESC")
    List<ReviewEntity> getReviewsByPlace(Long placeId);

    @Query("SELECT * FROM reviews WHERE userId = :userId ORDER BY createdAt DESC")
    List<ReviewEntity> getReviewsByUser(Long userId);

    @Query("SELECT * FROM reviews WHERE synced = 0 ORDER BY createdAt ASC")
    List<ReviewEntity> getPendingReviews();

    @Query("UPDATE reviews SET synced = 1, remoteId = :remoteId WHERE id = :localId")
    void markAsSynced(long localId, long remoteId);

    @Query("DELETE FROM reviews WHERE id = :reviewId")
    void delete(long reviewId);

    @Query("SELECT COUNT(*) FROM reviews WHERE synced = 0")
    int getPendingReviewsCount();

    @Query("SELECT * FROM reviews WHERE placeId = :placeId ORDER BY rating DESC")
    List<ReviewEntity> getReviewsByPlaceOrderByRatingDesc(Long placeId);

    @Query("SELECT * FROM reviews WHERE placeId = :placeId ORDER BY rating ASC")
    List<ReviewEntity> getReviewsByPlaceOrderByRatingAsc(Long placeId);

    @Query("SELECT * FROM reviews WHERE placeId = :placeId ORDER BY createdAt DESC")
    List<ReviewEntity> getReviewsByPlaceOrderByDateDesc(Long placeId);
}

