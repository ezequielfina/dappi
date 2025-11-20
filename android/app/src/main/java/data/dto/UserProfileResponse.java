package data.dto;

import com.google.gson.annotations.SerializedName;

public class UserProfileResponse {
    @SerializedName("userName")
    private String userName;

    @SerializedName("email")
    private String email;

    @SerializedName("profilePictureUrl")
    private String profilePictureUrl;

    @SerializedName("resenasRealizadas")
    private int resenasRealizadas;

    @SerializedName("upvotes")
    private Integer upvotes;

    @SerializedName("reviewMasVotada")
    private ReviewResponse reviewMasVotada;


    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public int getResenasRealizadas() {
        return resenasRealizadas;
    }

    public Integer getUpvotes() {
        return upvotes;
    }

    public ReviewResponse getReviewMasVotada() {
        return reviewMasVotada;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public void setResenasRealizadas(int resenasRealizadas) {
        this.resenasRealizadas = resenasRealizadas;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public void setReviewMasVotada(ReviewResponse reviewMasVotada) {
        this.reviewMasVotada = reviewMasVotada;
    }
}