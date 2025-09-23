package ar.edu.uade.api.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ar.edu.uade.api.R;
import ar.edu.uade.api.ui.login.LoginActivity;
import ar.edu.uade.api.ui.onboarding.OnboardingActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnRegister;
    private Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        btnSignup = findViewById(R.id.btn_signup);
    }

    private void setupClickListeners() {
        // Botón Registrarse - Valida y registra usuario
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    registerUser();
                }
            }
        });

        // Botón ¿Tienes cuenta? - Navega a Login
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Validar nombre
        if (TextUtils.isEmpty(etName.getText().toString().trim())) {
            etName.setError("El nombre es requerido");
            isValid = false;
        } else if (etName.getText().toString().trim().length() < 2) {
            etName.setError("El nombre debe tener al menos 2 caracteres");
            isValid = false;
        }

        // Validar email
        if (TextUtils.isEmpty(etEmail.getText().toString().trim())) {
            etEmail.setError("El email es requerido");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim()).matches()) {
            etEmail.setError("Ingresa un email válido");
            isValid = false;
        }

        // Validar teléfono
        if (TextUtils.isEmpty(etPhone.getText().toString().trim())) {
            etPhone.setError("El teléfono es requerido");
            isValid = false;
        } else if (etPhone.getText().toString().trim().length() < 8) {
            etPhone.setError("El teléfono debe tener al menos 8 dígitos");
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

        // Validar confirmación de contraseña
        if (TextUtils.isEmpty(etConfirmPassword.getText().toString())) {
            etConfirmPassword.setError("Confirma tu contraseña");
            isValid = false;
        } else if (!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
            etConfirmPassword.setError("Las contraseñas no coinciden");
            isValid = false;
        }

        return isValid;
    }

    private void registerUser() {
        // Aquí iría la lógica de registro con el backend
        // Por ahora simulamos un registro exitoso
        
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString();

        // Simular llamada al backend
        showLoading(true);
        
        // Simular delay de red
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showLoading(false);
                
                // Simular registro exitoso
                Toast.makeText(RegisterActivity.this, 
                    "¡Registro exitoso! Bienvenido " + name, 
                    Toast.LENGTH_SHORT).show();
                
                // Navegar al onboarding
                Intent intent = new Intent(RegisterActivity.this, OnboardingActivity.class);
                intent.putExtra("user_name", name);
                intent.putExtra("user_email", email);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        }, 2000);
    }

    private void showLoading(boolean show) {
        btnRegister.setEnabled(!show);
        btnRegister.setText(show ? "Registrando..." : "Registrarse");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
