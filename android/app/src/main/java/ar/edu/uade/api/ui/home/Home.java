package ar.edu.uade.api.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import ar.edu.uade.api.R;
import ar.edu.uade.api.ui.map.MapActivity;
import ar.edu.uade.api.ui.places.Place;
import ar.edu.uade.api.ui.user.ProfileActivity;

public class Home extends AppCompatActivity {

    private static final String TAG = "ExploreActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
    }
}