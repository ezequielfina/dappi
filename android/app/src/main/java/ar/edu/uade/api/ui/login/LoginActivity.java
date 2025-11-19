package ar.edu.uade.api.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ar.edu.uade.api.R;
import ar.edu.uade.api.ui.home.Home;

import data.session.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import data.api.AuthApi;
import data.api.RetrofitClient;
import data.dto.AuthenticationRequest;
import data.dto.AuthenticationResponse;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setupClickListeners();
        initializeSharedPreferences();
    }

    private void initializeViews() {
        etEmail = findViewById(R.id.et_username); // Cambiará a et_email en el XML
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    authenticateUser();
                }
            }
        });
    }

    private void initializeSharedPreferences() {
        sharedPreferences = getSharedPreferences("TravelGuidePrefs", MODE_PRIVATE);
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Validar email
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("El email es requerido");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Ingresa un email válido");
            isValid = false;
        }

        // Validar contraseña
        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            etPassword.setError("La contraseña es requerida");
            isValid = false;
        } else if (etPassword.getText().toString().length() < 6) {
            etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            isValid = false;
        }

        return isValid;
    }

    private void authenticateUser() {

        // Crear el objeto de petición
        AuthenticationRequest request = new AuthenticationRequest(
                etEmail.getText().toString().trim(),
                etPassword.getText().toString()
        );

        // Crear la instancia de la API SIN token (porque aún no tenemos)
        AuthApi api = RetrofitClient.getClient().create(AuthApi.class);

        // Hacer la petición de login
        api.login(request).enqueue(new Callback<AuthenticationResponse>() {
            @Override
            public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {



                if (response.isSuccessful() && response.body() != null) {
                    AuthenticationResponse authResponse = response.body();


                    // Guardar el token y datos del usuario
                    saveUserSession(authResponse);

                    Toast.makeText(
                            LoginActivity.this,
                            "¡Bienvenido!",
                            Toast.LENGTH_SHORT
                    ).show();

                    // Navegar a la pantalla principal
                    navigateToMainScreen();

                } else {
                    // Manejar diferentes códigos de error
                    String errorMsg;

                    switch (response.code()) {
                        case 401:
                        case 403:
                            errorMsg = "Usuario o contraseña incorrectos";
                            break;
                        case 404:
                            errorMsg = "Usuario no encontrado";
                            break;
                        case 500:
                            errorMsg = "Error en el servidor. Intenta más tarde";
                            break;
                        default:
                            errorMsg = "Error al iniciar sesión. Código: " + response.code();
                            break;
                    }

                    // Intentar obtener mensaje del servidor si existe
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            // Si el backend devuelve un mensaje en formato JSON
                            if (errorBody.contains("message")) {
                                // Aquí podrías parsear el JSON si tu backend lo envía estructurado
                                // Por ahora, mostramos el mensaje predeterminado
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(
                            LoginActivity.this,
                            errorMsg,
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                Toast.makeText(
                        LoginActivity.this,
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();

                t.printStackTrace();
            }
        });
    }

    private void saveUserSession(AuthenticationResponse authResponse) {
        SessionManager sessionManager = SessionManager.getInstance(this);
        sessionManager.saveSession(
                authResponse.getToken(),
                null, // userId (si el backend lo devuelve, pasalo acá)
                "", // username (se cargará después)
                etEmail.getText().toString().trim()
        );
    }
    private void navigateToMainScreen() {
        Intent intent = new Intent(LoginActivity.this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}