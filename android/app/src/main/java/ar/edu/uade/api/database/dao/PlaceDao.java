package ar.edu.uade.api.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ar.edu.uade.api.database.entity.PlaceEntity;

@Dao
public interface PlaceDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PlaceEntity place);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PlaceEntity> places);
    
    @Update
    void update(PlaceEntity place);
    
    @Query("SELECT * FROM places")
    List<PlaceEntity> getAllPlaces();
    
    @Query("SELECT * FROM places WHERE id = :placeId")
    PlaceEntity getPlaceById(long placeId);
    
    @Query("SELECT * FROM places WHERE isFavorite = 1")
    List<PlaceEntity> getFavoritePlaces();
    
    @Query("UPDATE places SET isFavorite = :favorite WHERE id = :placeId")
    void setFavorite(long placeId, boolean favorite);
    
    @Query("DELETE FROM places")
    void deleteAll();
    
    @Query("SELECT COUNT(*) FROM places")
    int getCount();
}

