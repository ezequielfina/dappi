package ar.edu.uade.api.ui.places;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ar.edu.uade.api.R;
import ar.edu.uade.api.network.ApiClient;

public class Review extends AppCompatActivity {
    
    private static final String TAG = "ReviewActivity";
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_LOCATION_PERMISSION = 101;
    private static final double MAX_DISTANCE_METERS = 500; // 500 metros

    // UI Components
    private ImageView backButton;
    private RatingBar ratingBar;
    private EditText reviewEditText;
    private Button takePhotoButton;
    private ImageView photoPreview;
    private View photoPreviewCard;
    private TextView locationStatus;
    private Button cancelButton;
    private Button submitButton;

    // Location and Photo
    private FusedLocationProviderClient fusedLocationClient;
    private double userLatitude = 0;
    private double userLongitude = 0;
    private Uri photoUri;
    private File photoFile;

    // API Client
    private ApiClient apiClient;

    // Place data (esto debería venir del intent en una versión completa)
    private long placeId = 1; // Hardcoded por ahora
    private long userId = 1; // Hardcoded por ahora - debería venir de SharedPreferences o sesión
    private double placeLatitude = -7.9425; // Mount Bromo
    private double placeLongitude = 112.9533;

    private ActivityResultLauncher<Intent> takePictureLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        initializeViews();
        setupLocationClient();
        setupPhotoLauncher();
        apiClient = new ApiClient();
        checkPermissionsAndValidateLocation();
        setupClickListeners();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.back_button_review);
        ratingBar = findViewById(R.id.rating_bar);
        reviewEditText = findViewById(R.id.review_edit_text);
        takePhotoButton = findViewById(R.id.take_photo_button);
        photoPreview = findViewById(R.id.photo_preview);
        photoPreviewCard = findViewById(R.id.photo_preview_card);
        locationStatus = findViewById(R.id.location_status);
        cancelButton = findViewById(R.id.cancel_button);
        submitButton = findViewById(R.id.submit_button);
    }

    private void setupLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void setupPhotoLauncher() {
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        try {
                            // Mostrar la foto tomada
                            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                            photoPreview.setImageBitmap(bitmap);
                            photoPreviewCard.setVisibility(View.VISIBLE);
                            Toast.makeText(this, "📷 Foto capturada exitosamente", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e(TAG, "Error al cargar foto", e);
                            Toast.makeText(this, "❌ Error al cargar la foto", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        
        cancelButton.setOnClickListener(v -> finish());

        takePhotoButton.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                openCamera();
            } else {
                requestCameraPermission();
            }
        });

        submitButton.setOnClickListener(v -> submitReview());
    }

    private void checkPermissionsAndValidateLocation() {
        // MODO DEMO: Simplemente obtener ubicación sin validar
        // El botón ENVIAR siempre está habilitado
        submitButton.setEnabled(true);
        
        if (checkLocationPermission()) {
            getLocationForDemo();
        } else {
            // No es crítico para demo, usar coordenadas por defecto
            userLatitude = placeLatitude;
            userLongitude = placeLongitude;
            locationStatus.setText("ℹ️ Modo Demo - Sin validación de ubicación");
        }
    }

    // ========== LOCATION VALIDATION ==========

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION_PERMISSION);
    }

    private void getLocationForDemo() {
        if (!checkLocationPermission()) {
            // Usar coordenadas del lugar como fallback
            userLatitude = placeLatitude;
            userLongitude = placeLongitude;
            locationStatus.setText("ℹ️ Modo Demo - Sin validación de ubicación");
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        userLatitude = location.getLatitude();
                        userLongitude = location.getLongitude();
                        
                        double distance = calculateDistance(
                                userLatitude, userLongitude,
                                placeLatitude, placeLongitude
                        );
                        
                        Log.d(TAG, "Distancia al lugar: " + distance + " metros (sin validar)");
                        locationStatus.setText("ℹ️ Modo Demo - Sin validación de ubicación");
                    } else {
                        // Usar coordenadas del lugar como fallback
                        userLatitude = placeLatitude;
                        userLongitude = placeLongitude;
                        locationStatus.setText("ℹ️ Modo Demo - Usando ubicación del lugar");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener ubicación (no crítico para demo)", e);
                    // Usar coordenadas del lugar como fallback
                    userLatitude = placeLatitude;
                    userLongitude = placeLongitude;
                    locationStatus.setText("ℹ️ Modo Demo - Usando ubicación del lugar");
                });
    }

    /**
     * Calcula la distancia en metros entre dos coordenadas GPS usando Haversine
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371000; // metros
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS * c;
    }

    // ========== CAMERA FUNCTIONALITY ==========

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_CAMERA_PERMISSION);
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(this,
                            "ar.edu.uade.api.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    takePictureLauncher.launch(takePictureIntent);
                }
            } catch (IOException ex) {
                Log.e(TAG, "Error al crear archivo de imagen", ex);
                Toast.makeText(this, "Error al crear archivo para la foto", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No hay aplicación de cámara disponible", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "REVIEW_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    // ========== SUBMIT REVIEW ==========

    private void submitReview() {
        // MODO DEMO: No validar ubicación, siempre permitir envío
        
        float rating = ratingBar.getRating();
        String reviewText = reviewEditText.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(this, "⭐ Por favor, selecciona una calificación", Toast.LENGTH_SHORT).show();
            return;
        }

        if (reviewText.isEmpty()) {
            Toast.makeText(this, "📝 Por favor, escribe tu reseña", Toast.LENGTH_SHORT).show();
            return;
        }

        // Deshabilitar botón para evitar múltiples envíos
        submitButton.setEnabled(false);
        submitButton.setText("ENVIANDO...");

        // Enviar reseña al backend
        apiClient.sendReviewWithPhoto(
                userId,
                placeId,
                reviewText,
                (int) rating,
                userLatitude,
                userLongitude,
                photoFile,
                new ApiClient.ApiCallback() {
                    @Override
                    public void onSuccess(String response) {
                        runOnUiThread(() -> {
                            Toast.makeText(Review.this, 
                                    "✅ Reseña enviada exitosamente!", 
                                    Toast.LENGTH_LONG).show();
                            finish();
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            Toast.makeText(Review.this, 
                                    "❌ Error al enviar reseña: " + error, 
                                    Toast.LENGTH_LONG).show();
                            submitButton.setEnabled(true);
                            submitButton.setText("ENVIAR");
                        });
                    }
                });
    }

    // ========== PERMISSIONS CALLBACK ==========

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationForDemo();
            } else {
                // MODO DEMO: No es crítico, usar coordenadas del lugar
                userLatitude = placeLatitude;
                userLongitude = placeLongitude;
                locationStatus.setText("ℹ️ Modo Demo - Usando ubicación del lugar");
                // El botón ENVIAR sigue habilitado
            }
        } else if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "📷 Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
