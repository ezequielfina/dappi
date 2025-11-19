package ar.edu.uade.api.ui.places;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import ar.edu.uade.api.R;

public class Place extends AppCompatActivity {

    private static final String TAG = "PlaceActivity";

    private ImageView placeImage;
    private TextView placeName;
    private TextView placeDescription;
    private Button leaveReviewButton;

    private Long placeId;
    private String name;
    private String description;
    private String imageUrl;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        initializeViews();
        getPlaceDataFromIntent();
        setupReviewButton();
    }

    private void initializeViews() {
        placeImage = findViewById(R.id.place_image);
        placeName = findViewById(R.id.place_title_expanded);
        placeDescription = findViewById(R.id.place_description);
        leaveReviewButton = findViewById(R.id.leave_review_button);
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

            if (name != null) {
                placeName.setText(name);
            }

            if (description != null) {
                placeDescription.setText(description);
            }

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

            // Pasar TODOS los datos necesarios
            intent.putExtra("place_id", placeId);
            intent.putExtra("place_name", name);
            intent.putExtra("place_latitude", latitude);
            intent.putExtra("place_longitude", longitude);

            startActivity(intent);
        });
    }
}