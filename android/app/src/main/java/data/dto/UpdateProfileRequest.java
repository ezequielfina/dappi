package data.dto;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileRequest {
    @SerializedName("userName")
    private String userName;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    public UpdateProfileRequest(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }
}
