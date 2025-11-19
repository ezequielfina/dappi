package ar.edu.uade.api.repository;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.List;

import ar.edu.uade.api.database.AppDatabase;
import ar.edu.uade.api.database.entity.ReviewEntity;
import ar.edu.uade.api.network.ApiClient;
import ar.edu.uade.api.network.NetworkManager;

public class ReviewRepository {
    
    private static final String TAG = "ReviewRepository";
    private static ReviewRepository instance;
    
    private final AppDatabase database;
    private final ApiClient apiClient;
    private final NetworkManager networkManager;
    private final Context context;
    
    private ReviewRepository(Context context) {
        this.context = context.getApplicationContext();
        this.database = AppDatabase.getInstance(this.context);
        this.apiClient = new ApiClient();
        this.networkManager = NetworkManager.getInstance(this.context);
    }
    
    public static synchronized ReviewRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ReviewRepository(context);
        }
        return instance;
    }
    
    /**
     * Crea una reseña. Si hay conexión, la envía al backend.
     * Si no hay conexión, la guarda localmente para sincronizar después.
     */
    public void createReview(
            long userId, 
            long placeId,
            String description,
            int rating,
            double latitude,
            double longitude,
            File photoFile,
            ReviewCallback callback
    ) {
        // Crear entidad local
        ReviewEntity review = new ReviewEntity();
        review.setUserId(userId);
        review.setPlaceId(placeId);
        review.setDescription(description);
        review.setRating(rating);
        review.setUserLatitude(latitude);
        review.setUserLongitude(longitude);
        review.setCreatedAt(System.currentTimeMillis());
        
        if (photoFile != null && photoFile.exists()) {
            review.setPhotoPath(photoFile.getAbsolutePath());
        }
        
        if (networkManager.isConnected()) {
            // CON CONEXIÓN: Enviar al backend
            Log.d(TAG, "🟢 Online: Enviando reseña al backend");
            review.setSynced(false); // Mientras se envía
            
            apiClient.sendReviewWithPhoto(
                    userId, placeId, description, rating,
                    latitude, longitude, photoFile,
                    new ApiClient.ApiCallback() {
                        @Override
                        public void onSuccess(String response) {
                            // Guardar en BD local como sincronizada
                            review.setSynced(true);
                            long localId = database.reviewDao().insert(review);
                            Log.d(TAG, "✅ Reseña sincronizada y guardada localmente");
                            callback.onSuccess("Reseña publicada exitosamente");
                        }
                        
                        @Override
                        public void onError(String error) {
                            // Si falla el backend, guardar localmente como pendiente
                            review.setSynced(false);
                            long localId = database.reviewDao().insert(review);
                            Log.w(TAG, "⚠️ Error al enviar, guardada localmente para sincronizar");
                            callback.onSavedOffline(
                                    "Reseña guardada. Se sincronizará cuando haya conexión.");
                        }
                    }
            );
        } else {
            // SIN CONEXIÓN: Guardar localmente
            Log.d(TAG, "🔴 Offline: Guardando reseña localmente");
            review.setSynced(false);
            long localId = database.reviewDao().insert(review);
            Log.d(TAG, "💾 Reseña guardada localmente con ID: " + localId);
            callback.onSavedOffline(
                    "Sin conexión. Reseña guardada y se sincronizará automáticamente.");
        }
    }
    
    /**
     * Obtiene reseñas de un lugar.
     * Primero intenta del backend, si falla usa caché local.
     */
    public List<ReviewEntity> getReviewsByPlace(long placeId) {
        if (networkManager.isConnected()) {
            // TODO: Llamar al backend y actualizar caché
            Log.d(TAG, "🟢 Online: Obteniendo reseñas del backend");
        }
        
        // Retornar del caché local
        return database.reviewDao().getReviewsByPlace(placeId);
    }
    
    /**
     * Sincroniza todas las reseñas pendientes con el backend
     */
    public void syncPendingReviews(SyncCallback callback) {
        if (!networkManager.isConnected()) {
            callback.onError("Sin conexión a internet");
            return;
        }
        
        List<ReviewEntity> pendingReviews = database.reviewDao().getPendingReviews();
        Log.d(TAG, "🔄 Sincronizando " + pendingReviews.size() + " reseñas pendientes");
        
        if (pendingReviews.isEmpty()) {
            callback.onComplete(0, 0);
            return;
        }
        
        int[] syncedCount = {0};
        int[] errorCount = {0};
        int totalReviews = pendingReviews.size();
        
        for (ReviewEntity review : pendingReviews) {
            File photoFile = null;
            if (review.getPhotoPath() != null) {
                photoFile = new File(review.getPhotoPath());
            }
            
            apiClient.sendReviewWithPhoto(
                    review.getUserId(),
                    review.getPlaceId(),
                    review.getDescription(),
                    review.getRating(),
                    review.getUserLatitude(),
                    review.getUserLongitude(),
                    photoFile,
                    new ApiClient.ApiCallback() {
                        @Override
                        public void onSuccess(String response) {
                            // Marcar como sincronizada
                            review.setSynced(true);
                            database.reviewDao().update(review);
                            syncedCount[0]++;
                            Log.d(TAG, "✅ Reseña " + review.getId() + " sincronizada");
                            
                            // Si es la última
                            if (syncedCount[0] + errorCount[0] == totalReviews) {
                                callback.onComplete(syncedCount[0], errorCount[0]);
                            }
                        }
                        
                        @Override
                        public void onError(String error) {
                            errorCount[0]++;
                            Log.e(TAG, "❌ Error sincronizando reseña " + review.getId());
                            
                            // Si es la última
                            if (syncedCount[0] + errorCount[0] == totalReviews) {
                                callback.onComplete(syncedCount[0], errorCount[0]);
                            }
                        }
                    }
            );
        }
    }
    
    public int getPendingReviewsCount() {
        return database.reviewDao().getPendingReviewsCount();
    }
    
    public interface ReviewCallback {
        void onSuccess(String message);
        void onSavedOffline(String message);
        void onError(String error);
    }
    
    public interface SyncCallback {
        void onComplete(int synced, int errors);
        void onError(String error);
    }
}

