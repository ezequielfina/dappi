package data.api;

import java.util.List;

import data.dto.ReviewRequest;

import data.dto.ReviewResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReviewApi {

    /**
     * Crear reseña sin foto
     */
    @POST("reviews")
    Call<ReviewResponse> createReview(@Body ReviewRequest request);

    /**
     * Crear reseña con foto(s)
     * Endpoint que tu amigo ya usa
     */
    @Multipart
    @POST("reviews/with-photos")
    Call<ReviewResponse> createReviewWithPhotos(
            @Part("review") RequestBody reviewJson,
            @Part List<MultipartBody.Part> photos
    );

    /**
     * Obtener reviews de un lugar
     */
    @GET("reviews/places/{placeId}")
    Call<List<ReviewResponse>> getReviewsByPlace(
            @Path("placeId") Long placeId,
            @Query("sortBy") String sortBy
    );

    @GET("reviews/places/{placeId}/top")
    Call<ReviewResponse> getTopReviewByPlace(@Path("placeId") Long placeId);

    /**
     * Obtener reviews del usuario actual
     */
    @GET("reviews/user/me")
    Call<List<ReviewResponse>> getMyReviews();

    @POST("reviews/{reviewId}/votes/up")
    Call<ReviewResponse> upVoteReview(@Path("reviewId") Long reviewId);

    @POST("reviews/{reviewId}/votes/down")
    Call<ReviewResponse> downVoteReview(@Path("reviewId") Long reviewId);
}