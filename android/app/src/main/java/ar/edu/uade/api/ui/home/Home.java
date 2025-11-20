package ar.edu.uade.api.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;
import ar.edu.uade.api.R;
import ar.edu.uade.api.ui.adapters.PlaceAdapter;
import ar.edu.uade.api.ui.user.ProfileActivity;
import data.api.RetrofitClient;
import data.dto.PlaceResponse;
import data.dto.UserProfileResponse;
import data.session.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private RecyclerView recyclerViewPlaces;
    private PlaceAdapter placeAdapter;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private ImageView profilePhoto;
    private TextView tvGreeting;
    private EditText searchView; // NUEVO

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessionManager = SessionManager.getInstance(this);
        initializeViews();
        setupSearchView(); // NUEVO
        loadUserProfile();
        setupRecyclerView();
        loadPlaces();
        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile();
    }

    private void initializeViews() {
        recyclerViewPlaces = findViewById(R.id.recycler_view_places);
        progressBar = findViewById(R.id.homeProgress);
        tvGreeting = findViewById(R.id.tvGreeting);
        profilePhoto = findViewById(R.id.avatar);
        searchView = findViewById(R.id.searchView);

        String username = sessionManager.getUsername();
        tvGreeting.setText("Hola, " + username);

        String cachedPhotoUrl = sessionManager.getProfilePictureUrl();
        if (cachedPhotoUrl != null) {
            loadProfileImage(cachedPhotoUrl);
        }

        if (profilePhoto != null) {
            profilePhoto.setOnClickListener(v -> {
                Log.d(TAG, "Profile photo clicked");
                try {
                    Intent intent = new Intent(Home.this, ProfileActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error starting ProfileActivity: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } else {
            Log.e(TAG, "avatar ImageView not found in layout");
        }
    }

    // NUEVO MÉTODO PARA CONFIGURAR LA BÚSQUEDA
    private void setupSearchView() {
        if (searchView == null) {
            Log.e(TAG, "SearchView not found in layout");
            return;
        }

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No necesitamos hacer nada aquí
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filtrar mientras el usuario escribe
                if (placeAdapter != null) {
                    placeAdapter.filter(s.toString());
                    Log.d(TAG, "Searching for: " + s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No necesitamos hacer nada aquí
            }
        });
    }

    private void setupRecyclerView() {
        placeAdapter = new PlaceAdapter(this);
        recyclerViewPlaces.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPlaces.setAdapter(placeAdapter);
    }

    private void loadPlaces() {
        progressBar.setVisibility(View.VISIBLE);

        String token = sessionManager.getToken();

        if (token == null || token.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "No se encontró token de autenticación", Toast.LENGTH_LONG).show();
            Log.e(TAG, "No auth token found");
            return;
        }

        Log.d(TAG, "Making API call to load places...");

        Call<List<PlaceResponse>> call = RetrofitClient.getPlaceApi(token).getAllPlaces();

        call.enqueue(new Callback<List<PlaceResponse>>() {
            @Override
            public void onResponse(Call<List<PlaceResponse>> call, Response<List<PlaceResponse>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<PlaceResponse> places = response.body();
                    placeAdapter.setPlaces(places);
                    Log.d(TAG, "✅ Loaded " + places.size() + " places");
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body from server: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }

                    String errorMsg = "Error al cargar lugares. Código: " + response.code();

                    if (response.code() == 401 || response.code() == 403) {
                        errorMsg = "Sesión expirada. Por favor inicia sesión nuevamente";
                    }

                    Toast.makeText(Home.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PlaceResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Error loading places: " + t.getMessage());
                Toast.makeText(Home.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupNavigation() {
        BottomNavigationView bottom = findViewById(R.id.bottomNav);
        bottom.setSelectedItemId(R.id.navigation_home);
        bottom.setOnItemReselectedListener(item -> {
        });
        bottom.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) return true;
            if (item.getItemId() == R.id.navigation_profile) {
                startActivity(new Intent(this, ProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                overridePendingTransition(0, 0);
                return true;
            }
            if (item.getItemId() == R.id.navigation_explore) {
                startActivity(new Intent(this, Home.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void loadUserProfile() {
        String token = sessionManager.getToken();

        if (token == null || token.isEmpty()) {
            Log.e(TAG, "No auth token found");
            return;
        }

        Call<UserProfileResponse> call = RetrofitClient.getUserApi(token).getMyProfile();

        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse profile = response.body();

                    sessionManager.saveUserProfile(
                            profile.getUserName(),
                            profile.getEmail(),
                            profile.getProfilePictureUrl(),
                            profile.getResenasRealizadas(),
                            profile.getUpvotes()
                    );

                    tvGreeting.setText("Hola, " + profile.getUserName());
                    loadProfileImage(profile.getProfilePictureUrl());

                } else {
                    Log.e(TAG, "Failed to load user profile: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Log.e(TAG, "Error loading user profile: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void loadProfileImage(String profilePictureUrl) {
        if (profilePhoto == null) {
            return;
        }
        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
            Log.d(TAG, "Loading profile image from: " + profilePictureUrl);

            Glide.with(this)
                    .load(profilePictureUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.ic_avatar_placeholder)
                    .error(R.drawable.ic_avatar_placeholder)
                    .circleCrop()
                    .into(profilePhoto);
        } else {
            Log.d(TAG, "No profile picture URL, using placeholder");
            profilePhoto.setImageResource(R.drawable.ic_avatar_placeholder);
        }
    }
}