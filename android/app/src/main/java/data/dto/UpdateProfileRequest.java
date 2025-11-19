package data.dto;

public class UpdateProfileRequest {
    private String country;
    private String favoritePlace;
    private String profileImageUrl;

    public UpdateProfileRequest() {
    }

    public UpdateProfileRequest(String country, String favoritePlace, String profileImageUrl) {
        this.country = country;
        this.favoritePlace = favoritePlace;
        this.profileImageUrl = profileImageUrl;
    }
}
