package ar.edu.uade.api.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;
import ar.edu.uade.api.R;
import ar.edu.uade.api.ui.adapters.PlaceAdapter;
import ar.edu.uade.api.ui.user.ProfileActivity;
import data.api.RetrofitClient;
import data.dto.PlaceResponse;
import data.dto.UserNameResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private RecyclerView recyclerViewPlaces;
    private PlaceAdapter placeAdapter;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("TravelGuidePrefs", MODE_PRIVATE);

        // Inicializar vistas
        initializeViews();

        loadUserName();

        // Configurar RecyclerView
        setupRecyclerView();

        // Cargar lugares desde el backend
        loadPlaces();

        // Configurar navegación
        setupNavigation();
    }

    private void initializeViews() {
        recyclerViewPlaces = findViewById(R.id.recycler_view_places);
        progressBar = findViewById(R.id.homeProgress);

        TextView tvGreeting = findViewById(R.id.tvGreeting);
        tvGreeting.setText("Hola...");

        ImageView profile_photo = findViewById(R.id.avatar);
        if (profile_photo != null) {
            profile_photo.setOnClickListener(v -> {
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

    private void setupRecyclerView() {
        placeAdapter = new PlaceAdapter(this);
        recyclerViewPlaces.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPlaces.setAdapter(placeAdapter);
    }

    private void loadPlaces() {
        progressBar.setVisibility(View.VISIBLE);

        String token = sharedPreferences.getString("auth_token", "");

        if (token.isEmpty()) {
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

    private void loadUserName() {
        String token = sharedPreferences.getString("auth_token", "");

        if (token.isEmpty()) {
            Log.e(TAG, "No auth token found");
            return;
        }

        TextView tvGreeting = findViewById(R.id.tvGreeting);

        Call<UserNameResponse> call = RetrofitClient.getUserApi(token).getMyUserName();

        call.enqueue(new Callback<UserNameResponse>() {
            @Override
            public void onResponse(Call<UserNameResponse> call, Response<UserNameResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String username = response.body().getUsername();
                    Log.e(TAG, "USERNAME" + username);

                    // Guardar en SharedPreferences
                    sharedPreferences.edit().putString("user_name", username).apply();

                    // Actualizar UI
                    tvGreeting.setText("Hola, " + username);
                } else {
                    Log.e(TAG, "Failed to load username: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserNameResponse> call, Throwable t) {
                Log.e(TAG, "Error loading username: " + t.getMessage());
            }
        });
    }

}