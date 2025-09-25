package ar.edu.uade.api.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import ar.edu.uade.api.ui.places.Place;
import ar.edu.uade.api.ui.user.User;
import ar.edu.uade.api.R;

public class Explore extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explore);

        FrameLayout card_mount = findViewById(R.id.place_card_mount);
        card_mount.setOnClickListener(v -> {
            Intent intent = new Intent(Explore.this, Place.class);
            startActivity(intent);
        });

        ImageView profile_photo = findViewById(R.id.avatar);
        card_mount.setOnClickListener(v -> {
            Intent intent = new Intent(Explore.this, User.class);
            startActivity(intent);
        });

        LinearLayout account_nav = findViewById(R.id.account_nav);
        card_mount.setOnClickListener(v -> {
            Intent intent = new Intent(Explore.this, Place.class);
            startActivity(intent);
        });
    }
}
