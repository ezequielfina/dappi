package ar.edu.uade.api.ui.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import ar.edu.uade.api.R;
import ar.edu.uade.api.databinding.ActivityProfileBinding;
import ar.edu.uade.api.ui.home.Home;
import ar.edu.uade.api.ui.map.MapActivity;
import data.dto.UserProfileResponse;
import data.repository.UserRepository;
import data.repository.callback.UserCallback;
import data.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private ActivityProfileBinding binding;
    private SessionManager sessionManager;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = SessionManager.getInstance(this);
        userRepository = UserRepository.getInstance(getApplicationContext());

        loadUserProfile();

        setSupportActionBar(binding.topAppBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        setupRecyclerViews();
        setupNavigation();
    }

    private void setupRecyclerViews() {

        List<Integer> flagImages = new ArrayList<>();
        flagImages.add(R.drawable.ic_flag_arg);

        FlagsFavoritesAdapter flagsAdapter = new FlagsFavoritesAdapter(this, flagImages);
        LinearLayoutManager flagsLM =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.rvFlags.setLayoutManager(flagsLM);
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
    }

    private void setupNavigation() {
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
                startActivity(new Intent(this, MapActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                overridePendingTransition(0,0);
                return true;
            }
            return false;
        });
    }

    private void loadUserProfile() {
        binding.profileProgress.setVisibility(View.VISIBLE);
        binding.profileContentLayout.setVisibility(View.GONE);

        String token = sessionManager.getToken();
        if (token == null || token.isEmpty()) {
            binding.profileProgress.setVisibility(View.GONE);
            Toast.makeText(this, "No se encontró token de autenticación", Toast.LENGTH_LONG).show();
            return;
        }

        userRepository.setAuthToken(token);

        userRepository.fetchUserProfile(new UserCallback() {
            @Override
            public void onSuccess(UserProfileResponse user) {
                binding.profileProgress.setVisibility(View.GONE);
                binding.profileContentLayout.setVisibility(View.VISIBLE);
                updateUIWithUserData(user);
            }

            @Override
            public void onError(String error) {
                binding.profileProgress.setVisibility(View.GONE);
                binding.profileContentLayout.setVisibility(View.VISIBLE);
                Log.e(TAG, "Error cargando perfil: " + error);
                Toast.makeText(ProfileActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateUIWithUserData(UserProfileResponse profile) {
        if (profile != null) {
            binding.tvProfileUsername.setText(profile.getUserName());
            binding.tvProfileEmail.setText(profile.getEmail());
            binding.tvReviewCount.setText(String.valueOf(profile.getResenasRealizadas()));
            binding.tvUpvoteCount.setText(profile.getUpvotes().toString());

            loadProfileImage(profile.getProfilePictureUrl());

            if (profile.getReviewMasVotada() != null) {
                binding.layoutPopularReview.setVisibility(View.VISIBLE);

                String desc = profile.getReviewMasVotada().getDescription();
                binding.tvPopularReviewDesc.setText(desc != null ? desc : "Sin descripción");

                Integer votes = profile.getReviewMasVotada().getReviewVotes();
                binding.tvPopularReviewVotes.setText(" " + (votes != null ? votes : 0) + " ");

                Integer rate = profile.getReviewMasVotada().getRateToPlace();
                binding.tvRateToPlace.setText(" " + (rate != null ? rate : null) + " ");

            } else {
                binding.layoutPopularReview.setVisibility(View.GONE);
            }
        }
    }

    private void loadProfileImage(String profilePictureUrl) {
        if (binding.ivProfilePhoto == null) return;

        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
            Glide.with(this)
                    .load(profilePictureUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.ic_avatar_placeholder)
                    .error(R.drawable.ic_avatar_placeholder)
                    .circleCrop()
                    .into(binding.ivProfilePhoto);
        } else {
            binding.ivProfilePhoto.setImageResource(R.drawable.ic_avatar_placeholder);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile_top, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_menu) {
            Intent intent = new Intent(this, ProfileConfigActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}