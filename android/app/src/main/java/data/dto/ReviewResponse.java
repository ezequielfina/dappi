package data.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("userId")
    private Long userId;

    @SerializedName("placeId")
    private Long placeId;

    @SerializedName("description")
    private String description;

    @SerializedName("rateToPlace")
    private int rateToPlace;

    @SerializedName("userLatitude")
    private Double userLatitude;

    @SerializedName("userLongitude")
    private Double userLongitude;

    @SerializedName("photos")
    private List<PhotoResponse> photos;

    @SerializedName("reviewVotes")
    private Integer reviewVotes;

    @SerializedName("createdAt")
    private String createdAt;

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getPlaceId() { return placeId; }
    public String getDescription() { return description; }
    public int getRateToPlace() { return rateToPlace; }
    public Double getUserLatitude() { return userLatitude; }
    public Double getUserLongitude() { return userLongitude; }
    public List<PhotoResponse> getPhotos() { return photos; }
    public String getCreatedAt() { return createdAt; }
    public Integer getReviewVotes() { return reviewVotes; }
}