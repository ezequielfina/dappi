package ar.edu.uade.api.ui.user;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import ar.edu.uade.api.R;
import ar.edu.uade.api.databinding.ActivityProfileConfigBinding;
import data.dto.UserProfileResponse;
import data.repository.UserRepository;
import data.repository.callback.UserCallback;
import data.session.SessionManager;

public class ProfileConfigActivity extends AppCompatActivity {

    private ActivityProfileConfigBinding binding;
    private SessionManager sessionManager;
    private UserRepository userRepository;

    private Uri selectedImageUri = null; // URI de la foto seleccionada en la galería

    // DEFINICIÓN DEL LANZADOR DE GALERÍA
    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    // Mostrar la imagen seleccionada inmediatamente en la UI
                    Glide.with(this).load(uri).circleCrop().into(binding.imgProfile);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileConfigBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. Inicializar dependencias y configurar token
        sessionManager = SessionManager.getInstance(this);
        userRepository = UserRepository.getInstance(this);
        userRepository.setAuthToken(sessionManager.getToken());

        // 2. Configurar Toolbar para que el botón de atrás funcione (Necesario al usar setSupportActionBar)
        setSupportActionBar(binding.topAppBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        binding.topAppBar.setNavigationOnClickListener(v -> onBackPressed());

        // Conectar el botón de foto
        binding.btnChangePhoto.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        // 3. CARGAR DATOS INICIALES
        loadDataFromSession();
        fetchFreshData();

        // 4. Configurar Botón Guardar
        binding.btnSaveChanges.setOnClickListener(v -> saveChanges());
    }

    private void loadDataFromSession() {
        String currentUsername = sessionManager.getUsername();
        String currentEmail = sessionManager.getEmail();
        String currentPhoto = sessionManager.getProfilePictureUrl();

        binding.etUsername.setText(currentUsername);
        binding.etEmail.setText(currentEmail);
        binding.etPassword.setText("");
        binding.etConfirmPassword.setText("");

        loadProfileImage(currentPhoto);
    }

    private void fetchFreshData() {
        userRepository.fetchUserProfile(new UserCallback() {
            @Override
            public void onSuccess(UserProfileResponse user) {
                binding.etUsername.setText(user.getUserName());
                binding.etEmail.setText(user.getEmail());
                loadProfileImage(user.getProfilePictureUrl());

                sessionManager.saveUserProfile(
                        user.getUserName(),
                        user.getEmail(),
                        user.getProfilePictureUrl(),
                        user.getResenasRealizadas(),
                        user.getUpvotes()
                );
            }

            @Override
            public void onError(String error) {
                // Notificar pero continuar con datos de sesión
            }
        });
    }

    private void loadProfileImage(String url) {
        if (url != null && !url.isEmpty()) {
            Glide.with(this)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.mipmap.avatar_foreground)
                    .error(R.mipmap.avatar_foreground)
                    .circleCrop()
                    .into(binding.imgProfile);
        } else {
            binding.imgProfile.setImageResource(R.mipmap.avatar_foreground);
        }
    }

    @SuppressLint("SetTextI18n")
    private void saveChanges() {
        String newUsername = binding.etUsername.getText().toString().trim();
        String newEmail = binding.etEmail.getText().toString().trim();
        String newPass = binding.etPassword.getText().toString().trim();
        String confirmPass = binding.etConfirmPassword.getText().toString().trim();

        // 1. Validaciones UI
        if (newUsername.isEmpty()) {
            binding.etUsername.setError("El usuario no puede estar vacío");
            return;
        }

        if (!newPass.isEmpty()) {
            if (!newPass.equals(confirmPass)) {
                binding.etConfirmPassword.setError("Las contraseñas no coinciden");
                return;
            }
            if (newPass.length() < 6) {
                binding.etPassword.setError("Mínimo 6 caracteres");
                return;
            }
        }

        // 2. Deshabilitar botón para evitar doble click
        binding.btnSaveChanges.setEnabled(false);
        binding.btnSaveChanges.setText("Guardando...");

        // 3. Llamada al Repositorio para guardar texto (PUT/PATCH)
        userRepository.updateUserProfile(newUsername, newEmail, newPass, new UserCallback() {
            @Override
            public void onSuccess(UserProfileResponse user) {
                // Actualizar la sesión local con datos de texto ya modificados
                sessionManager.saveUserProfile(
                        user.getUserName(),
                        user.getEmail(),
                        sessionManager.getProfilePictureUrl(), // Mantener foto actual por ahora
                        sessionManager.getReviewsCount(),
                        sessionManager.getUpvotes()
                );

                // 4. Si hay foto nueva seleccionada, subirla ahora
                if (selectedImageUri != null) {
                    uploadPhotoInternal();
                } else {
                    // Si no hay foto, terminamos el proceso de guardado
                    finishSuccess("Perfil actualizado correctamente");
                }
            }

            @Override
            public void onError(String error) {
                resetButton();
                Toast.makeText(ProfileConfigActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadPhotoInternal() {
        binding.btnSaveChanges.setText("Subiendo foto...");

        // Convertir Uri de la galería a un File temporal para Retrofit
        File file = uriToFile(selectedImageUri);

        if (file == null) {
            finishError("Error al preparar la imagen, datos de texto guardados.");
            return;
        }

        // Llamada al Repositorio para subir la imagen (Multipart)
        userRepository.uploadProfilePicture(file, new UserCallback() {
            @Override
            public void onSuccess(UserProfileResponse userWithPhoto) {
                // Éxito Total: Guardamos la URL de la nueva foto devuelta por el Backend
                sessionManager.saveUserProfile(
                        userWithPhoto.getUserName(),
                        userWithPhoto.getEmail(),
                        userWithPhoto.getProfilePictureUrl(), // ¡URL NUEVA!
                        userWithPhoto.getResenasRealizadas(),
                        userWithPhoto.getUpvotes()
                );
                // Eliminar el archivo temporal
                if (file.exists()) file.delete();

                finishSuccess("Perfil y foto actualizados");
            }

            @Override
            public void onError(String error) {
                finishError("Datos de texto guardados, pero falló la foto: " + error);
            }
        });
    }

    /**
     * Convierte una Uri de Android en un File temporal para poder subirlo con Retrofit.
     */
    private File uriToFile(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            // Usamos la carpeta caché para archivos temporales
            File tempFile = File.createTempFile("upload", ".jpg", getCacheDir());
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();
            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Métodos auxiliares
    private void finishSuccess(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        resetButton();
        finish();
    }

    private void finishError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        resetButton();
        finish();
    }

    private void resetButton() {
        binding.btnSaveChanges.setEnabled(true);
        binding.btnSaveChanges.setText("Guardar cambios");
    }
}