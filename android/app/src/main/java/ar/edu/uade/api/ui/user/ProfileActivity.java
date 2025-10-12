package ar.edu.uade.api.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ar.edu.uade.api.R;
import ar.edu.uade.api.databinding.ActivityProfileBinding;
import ar.edu.uade.api.ui.home.Home;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ---------- CONFIGURACIÓN DE TOOLBAR ----------
        setSupportActionBar(binding.topAppBar);
        if (getSupportActionBar() != null) {
            // NO mostrar flecha de retroceso por defecto
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        // ---------- SECCIÓN FAVORITOS ----------
        List<Integer> favoriteImages = new ArrayList<>();
        favoriteImages.add(R.drawable.ic_park_foreground);
        favoriteImages.add(R.drawable.ic_park_foreground);
        favoriteImages.add(R.drawable.ic_park_foreground);
        favoriteImages.add(R.drawable.ic_park_foreground);

        FavoritesAdapter favoritesAdapter = new FavoritesAdapter(this, favoriteImages);

        LinearLayoutManager favoritesNoScrollLM =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) {
                    @Override public boolean canScrollHorizontally() { return false; }
                };

        binding.rvFavorites.setLayoutManager(favoritesNoScrollLM);
        binding.rvFavorites.setHasFixedSize(true);
        binding.rvFavorites.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        binding.rvFavorites.setAdapter(favoritesAdapter);

        // ---------- SECCIÓN BANDERAS ----------
        List<Integer> flagImages = new ArrayList<>();
        flagImages.add(R.drawable.ic_flag_arg);
        flagImages.add(R.drawable.ic_flag_arg);
        flagImages.add(R.drawable.ic_flag_arg);
        flagImages.add(R.drawable.ic_flag_arg);
        flagImages.add(R.drawable.ic_flag_arg);

        FlagsFavoritesAdapter flagsAdapter = new FlagsFavoritesAdapter(this, flagImages);

        LinearLayoutManager flagsLM =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.rvFlags.setLayoutManager(flagsLM);
        binding.rvFlags.setHasFixedSize(true);
        binding.rvFlags.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        binding.rvFlags.setAdapter(flagsAdapter);

        binding.rvFlags.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return e.getAction() == MotionEvent.ACTION_MOVE;
            }
        });

        binding.rvFlags.post(() -> {
            int page = (int) (binding.rvFlags.getWidth() * 0.8f);
            binding.btnFlagsScrollLeft.setOnClickListener(v -> binding.rvFlags.smoothScrollBy(-page, 0));
            binding.btnFlagsScrollRight.setOnClickListener(v -> binding.rvFlags.smoothScrollBy(page, 0));
        });

        // ---------- BOTTOM NAV ----------
        BottomNavigationView bottom = findViewById(R.id.bottomNav);
        bottom.setSelectedItemId(R.id.navigation_profile);
        bottom.setOnItemReselectedListener(item -> {});
        bottom.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_profile) return true;
            if (item.getItemId() == R.id.navigation_home) {
                startActivity(new Intent(this, Home.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                overridePendingTransition(0,0);
                return true;
            }
            if (item.getItemId() == R.id.navigation_explore) {
                startActivity(new Intent(this, Home.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                overridePendingTransition(0,0);
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Infla el menú con el ícono de hamburguesa
        getMenuInflater().inflate(R.menu.menu_profile_top, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Manejar el click del menú hamburguesa
        if (item.getItemId() == R.id.action_menu) { // Asegúrate de que este ID coincida con el de tu menu XML
            Intent intent = new Intent(this, ProfileConfigActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}