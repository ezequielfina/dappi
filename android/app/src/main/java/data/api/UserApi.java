package data.api;

import data.dto.UpdateProfileRequest;
import data.dto.UserProfileResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface UserApi {

    @PUT("users/onboarding")
    Call<Void> completeOnboarding(@Body UpdateProfileRequest request);

    @PUT("users/me/update")
    Call<Void> updateProfile(@Body UpdateProfileRequest request);

    @GET("users/me")
    Call<UserProfileResponse> getMyProfile();

    @Multipart
    @POST("users/me/photo")
    Call<UserProfileResponse> uploadProfilePicture(@Part MultipartBody.Part photo);
}