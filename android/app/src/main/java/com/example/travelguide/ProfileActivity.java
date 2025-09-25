package com.example.travelguide;

import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelguide.databinding.ActivityProfileBinding;

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
            getSupportActionBar().setTitle(""); // Título vacío
            getSupportActionBar().setDisplayHomeAsUpEnabled(false); // No muestra botón atrás
        }

        // ---------- SECCIÓN FAVORITOS ----------
        // Lista de imágenes de prueba para favoritos
        List<Integer> favoriteImages = new ArrayList<>();
        favoriteImages.add(R.drawable.ic_park_foreground);
        favoriteImages.add(R.drawable.ic_park_foreground);
        favoriteImages.add(R.drawable.ic_park_foreground);
        favoriteImages.add(R.drawable.ic_park_foreground);

        FavoritesAdapter favoritesAdapter = new FavoritesAdapter(this, favoriteImages);

        // LayoutManager horizontal sin scroll (bloquea cualquier desplazamiento)
        LinearLayoutManager favoritesNoScrollLM =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) {
                    @Override public boolean canScrollHorizontally() { return false; }
                };

        binding.rvFavorites.setLayoutManager(favoritesNoScrollLM);
        binding.rvFavorites.setHasFixedSize(true);
        binding.rvFavorites.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        binding.rvFavorites.setAdapter(favoritesAdapter);

        // ---------- SECCIÓN BANDERAS ----------
        // Lista de imágenes de prueba para banderas
        List<Integer> flagImages = new ArrayList<>();
        flagImages.add(R.drawable.ic_flag_arg);
        flagImages.add(R.drawable.ic_flag_arg);
        flagImages.add(R.drawable.ic_flag_arg);
        flagImages.add(R.drawable.ic_flag_arg);
        flagImages.add(R.drawable.ic_flag_arg);

        FlagsFavoritesAdapter flagsAdapter = new FlagsFavoritesAdapter(this, flagImages);

        // LayoutManager horizontal con scroll habilitado (para las flechas)
        LinearLayoutManager flagsLM =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.rvFlags.setLayoutManager(flagsLM);
        binding.rvFlags.setHasFixedSize(true);
        binding.rvFlags.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        binding.rvFlags.setAdapter(flagsAdapter);

        // Bloquea el swipe manual pero permite clicks y scroll programado
        binding.rvFlags.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return e.getAction() == MotionEvent.ACTION_MOVE; // solo bloquea arrastre
            }
        });

        // Botones de flecha que mueven el carrusel de banderas por "páginas"
        binding.rvFlags.post(() -> {
            int page = (int) (binding.rvFlags.getWidth() * 0.8f);
            binding.btnFlagsScrollLeft.setOnClickListener(v -> binding.rvFlags.smoothScrollBy(-page, 0));
            binding.btnFlagsScrollRight.setOnClickListener(v -> binding.rvFlags.smoothScrollBy(page, 0));
        });

        // ---------- BOTTOM NAV ----------
        binding.bottomNav.setSelectedItemId(R.id.navigation_profile);
        binding.bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                return true;
            } else if (itemId == R.id.navigation_explore) {
                return true;
            } else if (itemId == R.id.navigation_profile) {
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Infla el menú superior del perfil
        getMenuInflater().inflate(R.menu.menu_profile_top, menu);
        return true;
    }
}
