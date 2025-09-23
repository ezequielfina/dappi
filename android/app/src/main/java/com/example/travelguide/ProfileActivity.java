package com.example.travelguide;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travelguide.databinding.ActivityProfileBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView; // Added import

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup para Toolbar
        setSupportActionBar(binding.topAppBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(""); // You already have this
        }
        // --- Setup para rvFavorites ---
        List<Integer> favoriteImages = new ArrayList<>();
        // Agregar lugares favoritos a la lista
        favoriteImages.add(R.drawable.ic_park_foreground);
        favoriteImages.add(R.drawable.ic_park_foreground);
        favoriteImages.add(R.drawable.ic_park_foreground);
        favoriteImages.add(R.drawable.ic_park_foreground);


        FavoritesAdapter favoritesAdapter = new FavoritesAdapter(this, favoriteImages);
        binding.rvFavorites.setAdapter(favoritesAdapter);

        // --- Setup para rvFlags ---
         List<Integer> flagImages = new ArrayList<>();
        // Agregar las banderas de los paises a la lista.
         flagImages.add(R.drawable.ic_flag_arg);
         flagImages.add(R.drawable.ic_flag_arg);
         flagImages.add(R.drawable.ic_flag_arg);

         FlagsFavoritesAdapter flagsAdapter = new FlagsFavoritesAdapter(this, flagImages);
         binding.rvFlags.setAdapter(flagsAdapter);

        // Set the selected item in BottomNavigationView
        binding.bottomNav.setSelectedItemId(R.id.navigation_profile); // Added this line
    }
}
