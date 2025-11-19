package ar.edu.uade.api.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import ar.edu.uade.api.R;
import ar.edu.uade.api.ui.map.MapActivity;
import ar.edu.uade.api.ui.places.Place;
import ar.edu.uade.api.ui.user.ProfileActivity;
import ar.edu.uade.api.network.NetworkManager;
import ar.edu.uade.api.repository.ReviewRepository;

public class Home extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private NetworkManager networkManager;
    private ReviewRepository reviewRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        networkManager = NetworkManager.getInstance(this);
        reviewRepository = ReviewRepository.getInstance(this);

        // Personalizar el saludo
        TextView tvGreeting = findViewById(R.id.tvGreeting);
        if (tvGreeting != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("TravelGuidePrefs", MODE_PRIVATE);
            String username = sharedPreferences.getString("username", "Usuario"); // "Usuario" como valor por defecto
            tvGreeting.setText("Hola, " + username);
        }

        // Clicks de tus cards/avatares con verificación de nulos
        ImageView card_mount = findViewById(R.id.place_card_mount);
        if (card_mount != null) {
            card_mount.setOnClickListener(v -> {
                Log.d(TAG, "Card mount clicked");
                startActivity(new Intent(Home.this, Place.class));
            });
        } else {
            Log.e(TAG, "place_card_mount not found in layout");
        }

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

        // Configuración del BottomNavigationView
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
                startActivity(new Intent(this, MapActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
        
        // Sincronizar reseñas pendientes si hay conexión
        syncPendingReviewsIfNeeded();
    }
    
    private void syncPendingReviewsIfNeeded() {
        if (!networkManager.isConnected()) {
            int pendingCount = reviewRepository.getPendingReviewsCount();
            if (pendingCount > 0) {
                Log.d(TAG, "📴 Sin conexión. " + pendingCount + " reseñas pendientes de sincronizar");
            }
            return;
        }
        
        int pendingCount = reviewRepository.getPendingReviewsCount();
        if (pendingCount == 0) {
            return; // No hay nada que sincronizar
        }
        
        Log.d(TAG, "🔄 Sincronizando " + pendingCount + " reseñas pendientes...");
        
        reviewRepository.syncPendingReviews(new ReviewRepository.SyncCallback() {
            @Override
            public void onComplete(int synced, int errors) {
                runOnUiThread(() -> {
                    if (synced > 0) {
                        Toast.makeText(Home.this, 
                                "✅ " + synced + " reseña(s) sincronizada(s)", 
                                Toast.LENGTH_LONG).show();
                    }
                    if (errors > 0) {
                        Toast.makeText(Home.this, 
                                "⚠️ " + errors + " reseña(s) con error", 
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Error en sincronización: " + error);
            }
        });
    }
}