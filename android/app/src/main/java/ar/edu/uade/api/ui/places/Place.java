package ar.edu.uade.api.ui.places;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ar.edu.uade.api.R;
import ar.edu.uade.api.ui.adapters.ReviewAdapter;
import data.api.RetrofitClient;
import data.dto.ReviewResponse;
import data.session.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Place extends AppCompatActivity {

    private static final String TAG = "PlaceActivity";

    private ImageView placeImage;
    private TextView placeName;
    private TextView placeDescription;
    private Button leaveReviewButton;

    // Tabs
    private TextView btnBestReview;
    private TextView btnWorstReview;
    private TextView btnLatestReview;

    // Reviews
    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;

    private Long placeId;
    private String name;
    private String description;
    private String imageUrl;
    private double latitude;
    private double longitude;

    private String currentSortBy = "best";

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        sessionManager = SessionManager.getInstance(this);

        initializeViews();
        getPlaceDataFromIntent();
        setupReviewButton();
        setupSortTabs();
        setupRecyclerView();

        // Cargar reviews iniciales
        loadReviews("best");
    }

    @SuppressLint("WrongViewCast")
    private void initializeViews() {
        placeImage = findViewById(R.id.place_image);
        placeName = findViewById(R.id.place_title_expanded);
        placeDescription = findViewById(R.id.place_description);
        leaveReviewButton = findViewById(R.id.leave_review_button);

        // Tabs
        btnBestReview = findViewById(R.id.btn_best_review);
        btnWorstReview = findViewById(R.id.btn_worst_review);
        btnLatestReview = findViewById(R.id.btn_latest_review);

        recyclerViewReviews = findViewById(R.id.recycler_reviews);
    }

    private void getPlaceDataFromIntent() {
        Intent intent = getIntent();

        if (intent != null) {

            placeId = intent.getLongExtra("place_id", -1L);
            name = intent.getStringExtra("place_name");
            description = intent.getStringExtra("place_description");
            imageUrl = intent.getStringExtra("place_url");
            latitude = intent.getDoubleExtra("place_latitude", 0);
            longitude = intent.getDoubleExtra("place_longitude", 0);

            if (name != null) placeName.setText(name);
            if (description != null) placeDescription.setText(description);

            if (imageUrl != null && placeImage != null) {
                Glide.with(this)
                        .load(imageUrl)
                        .centerCrop()
                        .into(placeImage);
            }
        }
    }

    private void setupReviewButton() {
        leaveReviewButton.setOnClickListener(v -> {
            Intent intent = new Intent(Place.this, Review.class);
            intent.putExtra("place_id", placeId);
            intent.putExtra("place_name", name);
            intent.putExtra("place_latitude", latitude);
            intent.putExtra("place_longitude", longitude);
            startActivity(intent);
        });
    }

    private void setupSortTabs() {

        btnBestReview.setOnClickListener(v -> {
            currentSortBy = "best";
            updateTabUI();
            loadReviews("best");
        });

        btnWorstReview.setOnClickListener(v -> {
            currentSortBy = "worst";
            updateTabUI();
            loadReviews("worst");
        });

        btnLatestReview.setOnClickListener(v -> {
            currentSortBy = "latest";
            updateTabUI();
            loadReviews("latest");
        });

        updateTabUI();
    }

    private void updateTabUI() {
        btnBestReview.setTextColor(getColor(R.color.text_secondary));
        btnWorstReview.setTextColor(getColor(R.color.text_secondary));
        btnLatestReview.setTextColor(getColor(R.color.text_secondary));

        btnBestReview.setBackground(null);
        btnWorstReview.setBackground(null);
        btnLatestReview.setBackground(null);

        switch (currentSortBy) {
            case "best":
                btnBestReview.setTextColor(getColor(R.color.accent_blue));
                btnBestReview.setBackgroundResource(R.drawable.tab_selected);
                break;
            case "worst":
                btnWorstReview.setTextColor(getColor(R.color.accent_blue));
                btnWorstReview.setBackgroundResource(R.drawable.tab_selected);
                break;
            case "latest":
                btnLatestReview.setTextColor(getColor(R.color.accent_blue));
                btnLatestReview.setBackgroundResource(R.drawable.tab_selected);
                break;
        }
    }

    private void setupRecyclerView() {
        reviewAdapter = new ReviewAdapter(this);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setAdapter(reviewAdapter);
    }

    private void loadReviews(String sortBy) {
        String token = sessionManager.getToken();

        Call<List<ReviewResponse>> call = RetrofitClient.getReviewApi(token)
                .getReviewsByPlace(placeId, sortBy);

        call.enqueue(new Callback<List<ReviewResponse>>() {
            @Override
            public void onResponse(Call<List<ReviewResponse>> call, Response<List<ReviewResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reviewAdapter.setReviews(response.body());
                    Log.d(TAG, "Loaded " + response.body().size() + " reviews (" + sortBy + ")");
                } else {
                    Toast.makeText(Place.this, "Error al cargar reseñas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ReviewResponse>> call, Throwable t) {
                Toast.makeText(Place.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
