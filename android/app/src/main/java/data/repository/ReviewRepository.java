package data.repository;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import data.api.RetrofitClient;
import data.api.ReviewApi;
import data.database.AppDatabase;
import data.database.entity.ReviewEntity;
import data.dto.ReviewRequest;
import data.dto.ReviewResponse;
import data.network.NetworkManager;
import data.repository.callback.ReviewCallback;
import data.repository.callback.ReviewListCallback;
import data.repository.callback.SyncCallback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewRepository {
    
    private static final String TAG = "ReviewRepository";
    private static ReviewRepository instance;
    
    private final AppDatabase database;
    private final NetworkManager networkManager;
    private final Context context;
    private String authToken;
    
    private ReviewRepository(Context context) {
        this.context = context.getApplicationContext();
        this.database = AppDatabase.getInstance(this.context);
        this.networkManager = NetworkManager.getInstance(this.context);
    }
    
    public static synchronized ReviewRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ReviewRepository(context);
        }
        return instance;
    }
    
    public void setAuthToken(String token) {
        this.authToken = token;
    }
    
    /**
     * Crear reseña con soporte offline
     * Si hay conexión → envía al backend
     * Si no hay conexión → guarda localmente para sincronizar después
     */
    public void createReview(
            Long userId, 
            Long placeId,
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
        
        if (networkManager.isConnected() && authToken != null) {
            // CON CONEXIÓN: Enviar al backend usando Retrofit
            Log.d(TAG, "🟢 Online: Enviando reseña al backend");
            sendReviewToBackend(userId, placeId, description, rating, 
                              latitude, longitude, photoFile, review, callback);
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
     * Enviar reseña al backend usando Retrofit
     */
    private void sendReviewToBackend(
            Long userId, Long placeId, String description, int rating,
            double latitude, double longitude, File photoFile,
            ReviewEntity localReview, ReviewCallback callback
    ) {
        ReviewApi api = RetrofitClient.getReviewApi(authToken);
        
        if (photoFile != null && photoFile.exists()) {
            // CON FOTO: Usar multipart
            sendReviewWithPhoto(api, userId, placeId, description, rating,
                              latitude, longitude, photoFile, localReview, callback);
        } else {
            // SIN FOTO: JSON simple
            ReviewRequest request = new ReviewRequest(
                userId, placeId, description, rating, latitude, longitude
            );
            
            api.createReview(request).enqueue(new Callback<ReviewResponse>() {
                @Override
                public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Guardar en BD local como sincronizada
                        localReview.setSynced(true);
                        localReview.setRemoteId(response.body().getId());
                        database.reviewDao().insert(localReview);
                        Log.d(TAG, "✅ Reseña sincronizada y guardada localmente");
                        callback.onSuccess("Reseña publicada exitosamente");
                    } else {
                        handleBackendError(localReview, callback);
                    }
                }
                
                @Override
                public void onFailure(Call<ReviewResponse> call, Throwable t) {
                    Log.e(TAG, "❌ Error de red: " + t.getMessage());
                    handleBackendError(localReview, callback);
                }
            });
        }
    }
    
    /**
     * Enviar reseña con foto usando multipart
     */
    private void sendReviewWithPhoto(
            ReviewApi api, Long userId, Long placeId, String description,
            int rating, double latitude, double longitude, File photoFile,
            ReviewEntity localReview, ReviewCallback callback
    ) {
        // Crear JSON de la reseña
        ReviewRequest reviewRequest = new ReviewRequest(
            userId, placeId, description, rating, latitude, longitude
        );
        String reviewJson = new Gson().toJson(reviewRequest);
        RequestBody reviewBody = RequestBody.create(
            MediaType.parse("application/json"), reviewJson
        );
        
        // Crear parte de la foto
        List<MultipartBody.Part> photoParts = new ArrayList<>();
        RequestBody photoBody = RequestBody.create(
            MediaType.parse("image/jpeg"), photoFile
        );
        MultipartBody.Part photoPart = MultipartBody.Part.createFormData(
            "photos", photoFile.getName(), photoBody
        );
        photoParts.add(photoPart);
        
        // Enviar
        api.createReviewWithPhotos(reviewBody, photoParts)
           .enqueue(new Callback<ReviewResponse>() {
               @Override
               public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                   if (response.isSuccessful() && response.body() != null) {
                       localReview.setSynced(true);
                       localReview.setRemoteId(response.body().getId());
                       database.reviewDao().insert(localReview);
                       Log.d(TAG, "✅ Reseña con foto sincronizada");
                       callback.onSuccess("Reseña publicada exitosamente");
                   } else {
                       handleBackendError(localReview, callback);
                   }
               }
               
               @Override
               public void onFailure(Call<ReviewResponse> call, Throwable t) {
                   Log.e(TAG, "❌ Error enviando foto: " + t.getMessage());
                   handleBackendError(localReview, callback);
               }
           });
    }
    
    /**
     * Manejar error del backend: guardar localmente para sincronizar después
     */
    private void handleBackendError(ReviewEntity localReview, ReviewCallback callback) {
        localReview.setSynced(false);
        database.reviewDao().insert(localReview);
        Log.w(TAG, "⚠️ Error al enviar, guardada localmente para sincronizar");
        callback.onSavedOffline(
            "Reseña guardada. Se sincronizará cuando haya conexión.");
    }
    
    /**
     * Obtener reseñas de un lugar
     * Intenta del backend, si falla usa caché local
     */
    public void getReviewsByPlace(Long placeId, ReviewListCallback callback) {
        if (networkManager.isConnected() && authToken != null) {
            ReviewApi api = RetrofitClient.getReviewApi(authToken);
            api.getReviewsByPlace(placeId).enqueue(new Callback<List<ReviewResponse>>() {
                @Override
                public void onResponse(Call<List<ReviewResponse>> call, Response<List<ReviewResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        // Si falla, usar caché local
                        useLocalCache(placeId, callback);
                    }
                }
                
                @Override
                public void onFailure(Call<List<ReviewResponse>> call, Throwable t) {
                    useLocalCache(placeId, callback);
                }
            });
        } else {
            useLocalCache(placeId, callback);
        }
    }
    
    private void useLocalCache(Long placeId, ReviewListCallback callback) {
        List<ReviewEntity> localReviews = database.reviewDao().getReviewsByPlace(placeId);
        Log.d(TAG, "📦 Usando caché local: " + localReviews.size() + " reseñas");
        callback.onLocalData(localReviews);
    }
    
    /**
     * Sincronizar todas las reseñas pendientes
     */
    public void syncPendingReviews(SyncCallback callback) {
        if (!networkManager.isConnected() || authToken == null) {
            callback.onError("Sin conexión o sin autenticación");
            return;
        }
        
        List<ReviewEntity> pendingReviews = database.reviewDao().getPendingReviews();
        Log.d(TAG, "🔄 Sincronizando " + pendingReviews.size() + " reseñas pendientes");
        
        if (pendingReviews.isEmpty()) {
            callback.onComplete(0, 0);
            return;
        }
        
        // Contador para tracking
        final int[] syncedCount = {0};
        final int[] errorCount = {0};
        final int totalReviews = pendingReviews.size();
        
        for (ReviewEntity review : pendingReviews) {
            File photoFile = review.getPhotoPath() != null ? 
                           new File(review.getPhotoPath()) : null;
            
            sendReviewToBackend(
                review.getUserId(),
                review.getPlaceId(),
                review.getDescription(),
                review.getRating(),
                review.getUserLatitude(),
                review.getUserLongitude(),
                photoFile,
                review,
                new ReviewCallback() {
                    @Override
                    public void onSuccess(String message) {
                        syncedCount[0]++;
                        checkSyncComplete();
                    }
                    
                    @Override
                    public void onSavedOffline(String message) {
                        errorCount[0]++;
                        checkSyncComplete();
                    }
                    
                    @Override
                    public void onError(String error) {
                        errorCount[0]++;
                        checkSyncComplete();
                    }
                    
                    private void checkSyncComplete() {
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

}