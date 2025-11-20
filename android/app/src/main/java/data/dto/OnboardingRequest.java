package data.dto;

import com.google.gson.annotations.SerializedName;

public class OnboardingRequest {
    @SerializedName("country")
    private String country;

    @SerializedName("favoritePlace")
    private String favoritePlace;

    @SerializedName("profileImageUrl")
    private String profileImageUrl;

    public OnboardingRequest(String country, String favoritePlace, String profileImageUrl) {
        this.country = country;
        this.favoritePlace = favoritePlace;
        this.profileImageUrl = profileImageUrl;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFavoritePlace() {
        return favoritePlace;
    }

    public void setFavoritePlace(String favoritePlace) {
        this.favoritePlace = favoritePlace;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}

