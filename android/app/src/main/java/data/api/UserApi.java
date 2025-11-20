package data.api;

import data.dto.UpdateProfileRequest;
import data.dto.UserProfileResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface UserApi {

    @PUT("users/onboarding")
    Call<Void> completeOnboarding(@Body UpdateProfileRequest request);

    @PUT("users/me/update")
    Call<Void> updateProfile(@Body UpdateProfileRequest request);

    @GET("users/me")
    Call<UserProfileResponse> getMyProfile();
}