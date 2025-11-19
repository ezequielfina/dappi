package data.dto;


import com.google.gson.annotations.SerializedName;

public class UserNameResponse {

    @SerializedName("userName")
    private String username;


    public String getUsername() {
        return username;
    }
}
