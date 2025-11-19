package ar.edu.uade.api.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ar.edu.uade.api.database.dao.PlaceDao;
import ar.edu.uade.api.database.dao.ReviewDao;
import ar.edu.uade.api.database.entity.PlaceEntity;
import ar.edu.uade.api.database.entity.ReviewEntity;

@Database(entities = {PlaceEntity.class, ReviewEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    
    private static final String DATABASE_NAME = "dappi_database";
    private static AppDatabase instance;
    
    public abstract PlaceDao placeDao();
    public abstract ReviewDao reviewDao();
    
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    DATABASE_NAME
            )
            .allowMainThreadQueries() // Solo para desarrollo/demo
            // En producción usar: .fallbackToDestructiveMigration()
            .build();
        }
        return instance;
    }
}

