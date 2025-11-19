package ar.edu.uade.api.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ar.edu.uade.api.database.entity.ReviewEntity;

@Dao
public interface ReviewDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ReviewEntity review);
    
    @Update
    void update(ReviewEntity review);
    
    @Query("SELECT * FROM reviews WHERE placeId = :placeId")
    List<ReviewEntity> getReviewsByPlace(long placeId);
    
    @Query("SELECT * FROM reviews WHERE userId = :userId")
    List<ReviewEntity> getReviewsByUser(long userId);
    
    @Query("SELECT * FROM reviews WHERE synced = 0")
    List<ReviewEntity> getPendingReviews();
    
    @Query("UPDATE reviews SET synced = 1, remoteId = :remoteId WHERE id = :localId")
    void markAsSynced(long localId, long remoteId);
    
    @Query("DELETE FROM reviews WHERE id = :reviewId")
    void delete(long reviewId);
    
    @Query("SELECT COUNT(*) FROM reviews WHERE synced = 0")
    int getPendingReviewsCount();
}

