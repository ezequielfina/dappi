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

    private Uri selectedImageUri = null;

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    Glide.with(this).load(uri).circleCrop().into(binding.imgProfile);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileConfigBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = SessionManager.getInstance(this);
        userRepository = UserRepository.getInstance(this);
        userRepository.setAuthToken(sessionManager.getToken());

        setSupportActionBar(binding.topAppBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        binding.topAppBar.setNavigationOnClickListener(v -> onBackPressed());

        binding.btnChangePhoto.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        loadDataFromSession();
        fetchFreshData();

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

        binding.btnSaveChanges.setEnabled(false);
        binding.btnSaveChanges.setText("Guardando...");

        userRepository.updateUserProfile(newUsername, newEmail, newPass, new UserCallback() {
            @Override
            public void onSuccess(UserProfileResponse user) {
                sessionManager.saveUserProfile(
                        user.getUserName(),
                        user.getEmail(),
                        sessionManager.getProfilePictureUrl(),
                        sessionManager.getReviewsCount(),
                        sessionManager.getUpvotes()
                );

                if (selectedImageUri != null) {
                    uploadPhotoInternal();
                } else {
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

        File file = uriToFile(selectedImageUri);

        if (file == null) {
            finishError("Error al preparar la imagen, datos de texto guardados.");
            return;
        }

        userRepository.uploadProfilePicture(file, new UserCallback() {
            @Override
            public void onSuccess(UserProfileResponse userWithPhoto) {
                sessionManager.saveUserProfile(
                        userWithPhoto.getUserName(),
                        userWithPhoto.getEmail(),
                        userWithPhoto.getProfilePictureUrl(),
                        userWithPhoto.getResenasRealizadas(),
                        userWithPhoto.getUpvotes()
                );
                if (file.exists()) file.delete();

                finishSuccess("Perfil y foto actualizados");
            }

            @Override
            public void onError(String error) {
                finishError("Datos de texto guardados, pero falló la foto: " + error);
            }
        });
    }

    private File uriToFile(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
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