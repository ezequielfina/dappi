package ar.edu.uade.api.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;

import ar.edu.uade.api.R;
import ar.edu.uade.api.ui.welcome.WelcomeActivity;

public class ProfileConfigActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_config);

        // Configurar toolbar con flecha de atrás
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // ===== LÓGICA PARA CERRAR SESIÓN (DIRECTO) =====
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            // Redirigir directamente a WelcomeActivity
            Intent intent = new Intent(ProfileConfigActivity.this, WelcomeActivity.class);
            // Limpiar el historial de actividades para que no pueda volver atrás
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // Cierra la actividad actual
        });
    }
}
