package data.dto;

import com.google.gson.annotations.SerializedName;

public class ReviewRequest {
    @SerializedName("userId")
    private Long userId;

    @SerializedName("placeId")
    private Long placeId;

    @SerializedName("description")
    private String description;

    @SerializedName("rateToPlace")
    private int rateToPlace;

    @SerializedName("userLatitude")
    private double userLatitude;

    @SerializedName("userLongitude")
    private double userLongitude;

    public ReviewRequest(Long userId, Long placeId, String description,
                         int rateToPlace, double userLatitude, double userLongitude) {
        this.userId = userId;
        this.placeId = placeId;
        this.description = description;
        this.rateToPlace = rateToPlace;
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
    }

    // Getters y Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getPlaceId() { return placeId; }
    public void setPlaceId(Long placeId) { this.placeId = placeId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getRateToPlace() { return rateToPlace; }
    public void setRateToPlace(int rateToPlace) { this.rateToPlace = rateToPlace; }

    public double getUserLatitude() { return userLatitude; }
    public void setUserLatitude(double userLatitude) { this.userLatitude = userLatitude; }

    public double getUserLongitude() { return userLongitude; }
    public void setUserLongitude(double userLongitude) { this.userLongitude = userLongitude; }
}