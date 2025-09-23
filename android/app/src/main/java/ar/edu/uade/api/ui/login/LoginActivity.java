package ar.edu.uade.api.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ar.edu.uade.api.R;
import ar.edu.uade.api.ui.register.RegisterActivity;
import ar.edu.uade.api.ui.welcome.WelcomeActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
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
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
    }

    private void setupClickListeners() {
        // Botón Iniciar Sesión - Valida y autentica usuario
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

        // Validar nombre de usuario
        if (TextUtils.isEmpty(etUsername.getText().toString().trim())) {
            etUsername.setError("El nombre de usuario es requerido");
            isValid = false;
        }

        // Validar contraseña
        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            etPassword.setError("La contraseña es requerida");
            isValid = false;
        }

        return isValid;
    }

    private void authenticateUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

        // Simular autenticación
        showLoading(true);

        // Simular delay de red
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showLoading(false);

                // Simular autenticación exitosa (en una app real, esto vendría del backend)
                if (isValidCredentials(username, password)) {
                    // Guardar sesión
                    saveUserSession(username);
                    
                    Toast.makeText(LoginActivity.this, 
                        "¡Bienvenido " + username + "!", 
                        Toast.LENGTH_SHORT).show();
                    
                    // Navegar a la pantalla principal (aquí irías a MainActivity o Dashboard)
                    navigateToMainScreen();
                } else {
                    Toast.makeText(LoginActivity.this, 
                        "Credenciales incorrectas", 
                        Toast.LENGTH_SHORT).show();
                }
            }
        }, 1500);
    }

    private boolean isValidCredentials(String username, String password) {
        // Simulación de validación (en una app real, esto se haría contra el backend)
        // Por ahora, aceptamos cualquier credencial que no esté vacía
        return !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password);
    }

    private void saveUserSession(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putBoolean("is_logged_in", true);
        editor.putLong("login_time", System.currentTimeMillis());
        editor.apply();
    }

    private void navigateToMainScreen() {
        // Por ahora volvemos a WelcomeActivity, pero aquí irías a tu pantalla principal
        Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void showLoading(boolean show) {
        btnLogin.setEnabled(!show);
        btnLogin.setText(show ? "Iniciando sesión..." : "Iniciar Sesion");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
