package data.dto;

import com.google.gson.annotations.SerializedName;

public class PhotoResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("uploadedAt")
    private String uploadedAt;

    public Long getId() { return id; }
    public String getImageUrl() { return imageUrl; }
    public String getUploadedAt() { return uploadedAt; }
}