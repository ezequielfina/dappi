package data.repository.callback;

import data.dto.UserProfileResponse;

public interface UserCallback {
    void onSuccess(UserProfileResponse user);
    void onError(String error);
}