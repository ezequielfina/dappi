package data.api;

import java.util.List;
import data.dto.PlaceResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PlaceApi {

    @GET("places")
    Call<List<PlaceResponse>> getAllPlaces();

    @GET("places/category/{category}")
    Call<List<PlaceResponse>> getPlacesByCategory(@Path("category") String category);

    @GET("places/{id}")
    Call<PlaceResponse> getPlaceById(@Path("id") Long placeId);
}
