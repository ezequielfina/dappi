package ar.edu.uade.api.ui.onboarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ar.edu.uade.api.R;
import ar.edu.uade.api.ui.welcome.WelcomeActivity;

public class OnboardingActivity extends AppCompatActivity {

    private Spinner spinnerCountry;
    private Button btnFavoritePlace;
    private Button btnProfilePhoto;
    private Button btnContinue;

    private String selectedCountry = "";
    private String selectedFavoritePlace = "";
    private boolean profilePhotoSelected = false;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        initializeViews();
        setupClickListeners();
        initializeSharedPreferences();
        loadUserData();
    }

    private void initializeViews() {
        spinnerCountry = findViewById(R.id.spinner_country);
        btnFavoritePlace = findViewById(R.id.btn_favorite_place);
        btnProfilePhoto = findViewById(R.id.btn_profile_photo);
        btnContinue = findViewById(R.id.btn_continue);
        
        setupCountrySpinner();
    }

    private void setupCountrySpinner() {
        // Crear adapter para el spinner de países
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
            this, R.array.countries, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(adapter);
        
        // Listener para cuando se selecciona un país
        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // No es el primer item "Seleccionar país"
                    selectedCountry = parent.getItemAtPosition(position).toString();
                    Toast.makeText(OnboardingActivity.this, 
                        "País seleccionado: " + selectedCountry, Toast.LENGTH_SHORT).show();
                } else {
                    selectedCountry = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCountry = "";
            }
        });
    }

    private void setupClickListeners() {
        // Botón Seleccionar Lugar Favorito
        btnFavoritePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFavoritePlaceSelection();
            }
        });

        // Botón Subir Foto de Perfil
        btnProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfilePhoto();
            }
        });

        // Botón Continuar
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateOnboarding()) {
                    completeOnboarding();
                }
            }
        });
    }

    private void initializeSharedPreferences() {
        sharedPreferences = getSharedPreferences("TravelGuidePrefs", MODE_PRIVATE);
    }

    private void loadUserData() {
        // Obtener datos del usuario desde el Intent
        Intent intent = getIntent();
        String userName = intent.getStringExtra("user_name");
        String userEmail = intent.getStringExtra("user_email");

        if (userName != null) {
            Toast.makeText(this, "¡Bienvenido " + userName + "!", Toast.LENGTH_SHORT).show();
        }
    }


    private void showFavoritePlaceSelection() {
        // Simular selección de lugar favorito
        String[] places = {"Playa", "Montaña", "Ciudad", "Campo", "Desierto", "Bosque"};
        
        // Por simplicidad, seleccionamos el primer lugar
        selectedFavoritePlace = places[0];
        btnFavoritePlace.setText(selectedFavoritePlace);
        
        Toast.makeText(this, "Lugar favorito seleccionado: " + selectedFavoritePlace, Toast.LENGTH_SHORT).show();
    }

    private void selectProfilePhoto() {
        // Simular selección de foto de perfil
        profilePhotoSelected = true;
        btnProfilePhoto.setText("✓");
        
        Toast.makeText(this, "Foto de perfil seleccionada", Toast.LENGTH_SHORT).show();
    }

    private boolean validateOnboarding() {
        if (selectedCountry.isEmpty()) {
            Toast.makeText(this, "Por favor selecciona tu país de origen", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedFavoritePlace.isEmpty()) {
            Toast.makeText(this, "Por favor selecciona tu lugar favorito", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!profilePhotoSelected) {
            Toast.makeText(this, "Por favor sube una foto de perfil", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void completeOnboarding() {
        // Guardar datos del onboarding
        saveOnboardingData();
        
        // Mostrar loading
        showLoading(true);
        
        // Simular delay de guardado
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showLoading(false);
                
                Toast.makeText(OnboardingActivity.this, 
                    "¡Onboarding completado! Bienvenido a TravelGuide", 
                    Toast.LENGTH_SHORT).show();
                
                // Navegar a la pantalla principal
                navigateToMainScreen();
            }
        }, 1500);
    }

    private void saveOnboardingData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_country", selectedCountry);
        editor.putString("user_favorite_place", selectedFavoritePlace);
        editor.putBoolean("profile_photo_selected", profilePhotoSelected);
        editor.putBoolean("onboarding_completed", true);
        editor.apply();
    }

    private void navigateToMainScreen() {
        // Por ahora volvemos a WelcomeActivity, pero aquí irías a tu pantalla principal
        Intent intent = new Intent(OnboardingActivity.this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void showLoading(boolean show) {
        btnContinue.setEnabled(!show);
        btnContinue.setText(show ? "Completando..." : "Continuar");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
