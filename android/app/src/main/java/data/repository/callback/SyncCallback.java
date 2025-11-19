package data.repository.callback;

public interface SyncCallback {
        void onComplete(int synced, int errors);
        void onError(String error);
    }