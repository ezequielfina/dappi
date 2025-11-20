package ar.edu.uade.api.ui.onboarding;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import ar.edu.uade.api.R;

import ar.edu.uade.api.ui.login.LoginActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import data.api.RetrofitClient;
import data.api.UserApi;
import data.dto.OnboardingRequest;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class OnboardingActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;

    private Spinner spinnerCountry;
    private Button btnFavoritePlace;
    private Button btnProfilePhoto;
    private Button btnContinue;
    private ImageView ivProfilePreview;

    private String selectedCountry = "";
    private String selectedFavoritePlace = "";
    private String profilePictureBase64 = "";
    private boolean profilePhotoSelected = false;

    private SharedPreferences sharedPreferences;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        initializeViews();
        setupImagePicker();
        setupClickListeners();
        initializeSharedPreferences();
        loadUserData();
    }

    private void initializeViews() {
        spinnerCountry = findViewById(R.id.spinner_country);
        btnFavoritePlace = findViewById(R.id.btn_favorite_place);
        btnProfilePhoto = findViewById(R.id.btn_profile_photo);
        btnContinue = findViewById(R.id.btn_continue);
        ivProfilePreview = findViewById(R.id.iv_profile_preview);

        setupCountrySpinner();
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            handleImageSelection(imageUri);
                        }
                    }
                }
        );
    }

    private void setupCountrySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.countries, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(adapter);

        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
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
        btnFavoritePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFavoritePlaceSelection();
            }
        });

        btnProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfilePhoto();
            }
        });

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
        Intent intent = getIntent();
        String userName = intent.getStringExtra("user_name");
        String userEmail = intent.getStringExtra("user_email");

        if (userName != null) {
            Toast.makeText(this, "¡Bienvenido " + userName + "!", Toast.LENGTH_SHORT).show();
        }

        if (userEmail != null) {
            sharedPreferences.edit().putString("user_email", userEmail).apply();
        }
    }

    private void showFavoritePlaceSelection() {
        String[] places = {"Playa", "Montaña", "Ciudad", "Campo", "Desierto", "Bosque"};

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Selecciona tu lugar favorito")
                .setItems(places, (dialog, which) -> {
                    selectedFavoritePlace = places[which];
                    btnFavoritePlace.setText(selectedFavoritePlace);
                    Toast.makeText(this, "Lugar seleccionado: " + selectedFavoritePlace, Toast.LENGTH_SHORT).show();
                })
                .show();
    }
    private void selectProfilePhoto() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_CODE);
            } else {
                openImagePicker();
            }
        } else {
            // Android 12 y anteriores usan READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                openImagePicker();
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Permiso denegado. No se puede acceder a la galería.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void handleImageSelection(Uri imageUri) {
        try {

            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // Redimensionar la imagen para no enviar archivos muy grandes
            Bitmap resizedBitmap = resizeBitmap(bitmap, 800, 800);

            // Convertir a Base64
            profilePictureBase64 = bitmapToBase64(resizedBitmap);

            profilePhotoSelected = true;
            btnProfilePhoto.setText("✓ Foto seleccionada");

            ivProfilePreview.setImageBitmap(resizedBitmap);
            ivProfilePreview.setVisibility(View.VISIBLE);

            Toast.makeText(this, "Foto de perfil seleccionada", Toast.LENGTH_SHORT).show();

            Log.d("OnboardingActivity", "Imagen convertida a Base64. Tamaño: " + profilePictureBase64.length());

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            Log.e("OnboardingActivity", "Error al procesar imagen", e);
        }
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float ratioBitmap = (float) width / (float) height;
        float ratioMax = (float) maxWidth / (float) maxHeight;

        int finalWidth = maxWidth;
        int finalHeight = maxHeight;

        if (ratioMax > ratioBitmap) {
            finalWidth = (int) ((float) maxHeight * ratioBitmap);
        } else {
            finalHeight = (int) ((float) maxWidth / ratioBitmap);
        }

        return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true);
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
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
        showLoading(true);

        Log.d("OnboardingActivity", "Completando onboarding...");
        Log.d("OnboardingActivity", "País: " + selectedCountry);
        Log.d("OnboardingActivity", "Lugar favorito: " + selectedFavoritePlace);


        OnboardingRequest request = new OnboardingRequest(
                selectedCountry,
                selectedFavoritePlace,
                "data:image/jpeg;base64," + profilePictureBase64
        );


        String token = sharedPreferences.getString("auth_token", "");
        Log.d("OnboardingActivity", "Token: " + token.substring(0, Math.min(20, token.length())) + "...");

        if (token.isEmpty()) {
            Toast.makeText(this, "Error: No hay token de autenticación", Toast.LENGTH_LONG).show();
            showLoading(false);
            return;
        }

        // Llamar a la API CON autenticación
        UserApi userApi = RetrofitClient.getClientWithAuth(token).create(UserApi.class);

        userApi.completeOnboarding(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                showLoading(false);

                if (response.isSuccessful()) {
                    saveOnboardingData();

                    Toast.makeText(OnboardingActivity.this,
                            "¡Perfil completado!",
                            Toast.LENGTH_SHORT).show();

                    navigateToMainScreen();
                } else {
                    String errorMsg = "Error al guardar el perfil: " + response.code();

                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e("OnboardingActivity", "Error body: " + errorBody);
                            errorMsg = "Error: " + errorBody;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(OnboardingActivity.this,
                            errorMsg,
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showLoading(false);

                Toast.makeText(OnboardingActivity.this,
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();

                Log.e("OnboardingActivity", "Error de red", t);
                t.printStackTrace();
            }
        });
    }

    private void saveOnboardingData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_country", selectedCountry);
        editor.putString("user_favorite_place", selectedFavoritePlace);
        editor.putString("user_profile_picture", profilePictureBase64);
        editor.putBoolean("onboarding_completed", true);
        editor.apply();
    }

    private void navigateToMainScreen() {
        Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Toast.makeText(this,
                "Perfil completado. Ahora inicia sesión",
                Toast.LENGTH_LONG).show();

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