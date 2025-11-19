package data.repository.callback;

public interface ReviewCallback {
    void onSuccess(String message);
    void onSavedOffline(String message);
    void onError(String error);
}