package data.api;

import data.dto.AuthenticationRequest;
import data.dto.AuthenticationResponse;
import data.dto.RegisterRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {

    @POST("auth/register")
    Call<AuthenticationResponse> register(@Body RegisterRequest request);

    @POST("auth/authenticate")
    Call<AuthenticationResponse> login(@Body AuthenticationRequest request);
}
