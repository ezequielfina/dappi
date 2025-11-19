package ar.edu.uade.api.ui.maps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;

import ar.edu.uade.api.R;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Configurar toolbar
        toolbar = findViewById(R.id.topAppBar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // Inicializar FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtener referencia al mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        // Habilitar controles de UI
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true); // Usar botón integrado de Google Maps

        // Verificar y solicitar permisos de ubicación
        if (hasLocationPermission()) {
            enableMyLocation();
        } else {
            requestLocationPermission();
        }

        // Configurar click listener en el mapa
        googleMap.setOnMapClickListener(latLng -> {
            // Agregar marcador donde se hace click
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Ubicación seleccionada")
                    .snippet(latLng.latitude + ", " + latLng.longitude));
        });

        // Posición inicial - Buenos Aires, Argentina
        LatLng buenosAires = new LatLng(-34.6037, -58.3816);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(buenosAires, 13f));
        googleMap.addMarker(new MarkerOptions()
                .position(buenosAires)
                .title("Buenos Aires")
                .snippet("Ciudad Autónoma de Buenos Aires"));
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE
        );
    }

    private void enableMyLocation() {
        if (hasLocationPermission() && googleMap != null) {
            try {
                googleMap.setMyLocationEnabled(true);
            } catch (SecurityException e) {
                Toast.makeText(this, "Error al habilitar ubicación", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void centerMapOnMyLocation() {
        if (!hasLocationPermission()) {
            Toast.makeText(this, "Permiso de ubicación no concedido", Toast.LENGTH_SHORT).show();
            requestLocationPermission();
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
                            LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f));
                            Toast.makeText(MapActivity.this, "Centrado en tu ubicación", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MapActivity.this, "No se pudo obtener tu ubicación", Toast.LENGTH_SHORT).show();
                            // Mover a Buenos Aires como fallback
                            LatLng buenosAires = new LatLng(-34.6037, -58.3816);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(buenosAires, 13f));
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(this, "Se necesitan permisos de ubicación para usar esta función", Toast.LENGTH_LONG).show();
            }
        }
    }
}

