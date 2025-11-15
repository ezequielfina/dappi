package ar.edu.uade.api.ui.places;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ar.edu.uade.api.R;
import ar.edu.uade.api.ui.map.MapActivity;

public class Place extends AppCompatActivity {

    private double placeLatitude = -7.9425; // Latitud de Mount Bromo
    private double placeLongitude = 112.9533; // Longitud de Mount Bromo
    private String placeTitle = "Mount Bromo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        // Asignar título del lugar (esto debería venir de datos reales)
        TextView titleView = findViewById(R.id.place_title_expanded);
        if (titleView != null) {
            titleView.setText(placeTitle);
        }

        // Botón para dejar una reseña
        Button leaveReviewButton = findViewById(R.id.leave_review_button);
        leaveReviewButton.setOnClickListener(v -> {
            Intent intent = new Intent(Place.this, Review.class);
            startActivity(intent);
        });

        // Botón para ver la ubicación en el mapa
        Button mapButton = findViewById(R.id.map_button);
        mapButton.setOnClickListener(v -> {
            Intent intent = new Intent(Place.this, MapActivity.class);
            intent.putExtra("latitude", placeLatitude);
            intent.putExtra("longitude", placeLongitude);
            intent.putExtra("title", placeTitle);
            startActivity(intent);
        });
    }
}