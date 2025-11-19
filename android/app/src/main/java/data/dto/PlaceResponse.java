package data.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaceResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("full_address")
    private String fullAddress;

    @SerializedName("latitude")
    private Double latitude;

    @SerializedName("longitude")
    private Double longitude;

    @SerializedName("description")
    private String description;

    @SerializedName("url")
    private String url;

    @SerializedName("placeCategory")
    private String placeCategory;

    // Campos adicionales del modelo de tu amigo
    @SerializedName("entryTime")
    private String entryTime;

    @SerializedName("endTime")
    private String endTime;

    @SerializedName("instagram")
    private String instagram;

    @SerializedName("website")
    private String website;

    @SerializedName("placeType")
    private String placeType;

    @SerializedName("reviews")
    private List<ReviewResponse> reviews;

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getFullAddress() { return fullAddress; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public String getDescription() { return description; }
    public String getUrl() { return url; }
    public String getPlaceCategory() { return placeCategory; }
    public String getEntryTime() { return entryTime; }
    public String getEndTime() { return endTime; }
    public String getInstagram() { return instagram; }
    public String getWebsite() { return website; }
    public String getPlaceType() { return placeType; }
    public List<ReviewResponse> getReviews() { return reviews; }
}
