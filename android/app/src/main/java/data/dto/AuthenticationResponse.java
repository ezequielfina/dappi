package data.dto;

import com.google.gson.annotations.SerializedName;

public class AuthenticationResponse {
    @SerializedName("token")
    private String token;

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
