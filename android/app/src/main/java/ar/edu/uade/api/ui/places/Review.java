package ar.edu.uade.api.ui.places;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import ar.edu.uade.api.R;

public class Review extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        // Botón para volver atrás
        ImageView backButton = findViewById(R.id.back_button_review);
        backButton.setOnClickListener(v -> {
            finish(); // Cierra la actividad actual y vuelve a la anterior (Place)
        });

        // Botón para cancelar
        Button cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> {
            finish(); // Cierra la actividad actual y vuelve a la anterior (Place)
        });
    }
}
