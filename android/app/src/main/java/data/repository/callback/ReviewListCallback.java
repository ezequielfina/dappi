package data.repository.callback;

import java.util.List;

import data.database.entity.ReviewEntity;
import data.dto.ReviewResponse;

public interface ReviewListCallback {
        void onSuccess(List<ReviewResponse> reviews);
        void onLocalData(List<ReviewEntity> localReviews);
        void onError(String error);
    }