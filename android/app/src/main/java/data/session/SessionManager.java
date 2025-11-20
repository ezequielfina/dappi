package data.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SessionManager {
    private static final String TAG = "SessionManager";
    private static final String PREF_NAME = "TravelGuidePrefs";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_PROFILE_PICTURE_URL = "profile_picture_url";
    private static final String KEY_REVIEWS_COUNT = "reviews_count";
    private static final String KEY_UPVOTES = "upvotes";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private static SessionManager instance;
    private SharedPreferences prefs;

    private SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveSession(String token, Long userId, String username, String email) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_TOKEN, token);
        if (userId != null) {
            editor.putLong(KEY_USER_ID, userId);
        }
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();

        Log.d(TAG, "Sesión guardada - User: " + username);
    }

    public void saveUserProfile(String username, String email, String profilePictureUrl,
                                int reviewsCount, int upvotes) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PROFILE_PICTURE_URL, profilePictureUrl);
        editor.putInt(KEY_REVIEWS_COUNT, reviewsCount);
        editor.putInt(KEY_UPVOTES, upvotes);
        editor.apply();

        Log.d(TAG, "Perfil actualizado - User: " + username);
    }

    public void updateProfilePicture(String profilePictureUrl) {
        prefs.edit()
                .putString(KEY_PROFILE_PICTURE_URL, profilePictureUrl)
                .apply();
        Log.d(TAG, "Foto de perfil actualizada");
    }

    public void clearSession() {
        prefs.edit().clear().apply();
        Log.d(TAG, "Sesión cerrada");
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public Long getUserId() {
        long id = prefs.getLong(KEY_USER_ID, -1L);
        return id == -1L ? null : id;
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "Usuario");
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public String getProfilePictureUrl() {
        return prefs.getString(KEY_PROFILE_PICTURE_URL, null);
    }

    public int getReviewsCount() {
        return prefs.getInt(KEY_REVIEWS_COUNT, 0);
    }

    public int getUpvotes() {
        return prefs.getInt(KEY_UPVOTES, 0);
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false) && getToken() != null;
    }
}