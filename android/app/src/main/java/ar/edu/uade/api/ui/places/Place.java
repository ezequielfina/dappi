package ar.edu.uade.api.ui.places;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import ar.edu.uade.api.R;

public class Place extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place);

        Button leaveReviewButton = findViewById(R.id.leave_review_button);
        leaveReviewButton.setOnClickListener(v -> {
            Intent intent = new Intent(Place.this, Review.class);
            startActivity(intent);
        });
    }

}
