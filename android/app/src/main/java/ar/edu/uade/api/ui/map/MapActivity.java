package ar.edu.uade.api.ui.map;

import android.content.Intent;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import ar.edu.uade.api.R;
import ar.edu.uade.api.ui.home.Home;
import ar.edu.uade.api.ui.user.ProfileActivity;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        setupBottomNavigation();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Configuración básica del mapa
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        // Revisar permisos y habilitar la capa "Mi Ubicación"
        if (checkLocationPermission()) {
            enableMyLocation();
        } else {
            requestLocationPermission();
        }

        // Comprobar si se pasó una ubicación específica para mostrar
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("latitude") && intent.hasExtra("longitude")) {
            double latitude = intent.getDoubleExtra("latitude", 0);
            double longitude = intent.getDoubleExtra("longitude", 0);
            String title = intent.getStringExtra("title");
            if (title == null) {
                title = "Ubicación seleccionada";
            }

            LatLng placeLocation = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(placeLocation).title(title));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLocation, 15));
        } else {
            // Si no, mostrar la ubicación actual del usuario
            showCurrentLocation();
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottom = findViewById(R.id.bottomNav);
        bottom.setSelectedItemId(R.id.navigation_explore);
        bottom.setOnItemReselectedListener(item -> {
        });
        bottom.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_explore) return true;
            if (item.getItemId() == R.id.navigation_home) {
                startActivity(new Intent(this, Home.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                overridePendingTransition(0, 0);
                return true;
            }
            if (item.getItemId() == R.id.navigation_profile) {
                startActivity(new Intent(this, ProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
                showCurrentLocation();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
                // Si se niegan los permisos, mostrar una ubicación por defecto
                showDefaultLocation();
            }
        }
    }

    private void enableMyLocation() {
        if (mMap != null && checkLocationPermission()) {
            try {
                mMap.setMyLocationEnabled(true);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    private void showCurrentLocation() {
        if (checkLocationPermission()) {
            Task<Location> locationTask = fusedLocationClient.getLastLocation();
            locationTask.addOnSuccessListener(location -> {
                if (location != null && mMap != null) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                } else {
                    // No se pudo obtener la ubicación, mostrar la de por defecto
                    showDefaultLocation();
                }
            });
        } else {
            showDefaultLocation();
        }
    }
    
    private void showDefaultLocation(){
        if (mMap == null) return;
        LatLng buenosAires = new LatLng(-34.6037, -58.3816);
        mMap.addMarker(new MarkerOptions()
                .position(buenosAires)
                .title("Buenos Aires"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(buenosAires, 12));
    }
}
