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
import data.session.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import data.api.AuthApi;
import data.api.RetrofitClient;
import data.dto.AuthenticationResponse;
import data.dto.RegisterRequest;

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
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    registerUser();
                }
            }
        });

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

        if (TextUtils.isEmpty(etName.getText().toString().trim())) {
            etName.setError("El nombre es requerido");
            isValid = false;
        } else if (etName.getText().toString().trim().length() < 2) {
            etName.setError("El nombre debe tener al menos 2 caracteres");
            isValid = false;
        }

        if (TextUtils.isEmpty(etEmail.getText().toString().trim())) {
            etEmail.setError("El email es requerido");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim()).matches()) {
            etEmail.setError("Ingresa un email válido");
            isValid = false;
        }

        if (TextUtils.isEmpty(etPhone.getText().toString().trim())) {
            etPhone.setError("El teléfono es requerido");
            isValid = false;
        } else if (etPhone.getText().toString().trim().length() < 8) {
            etPhone.setError("El teléfono debe tener al menos 8 dígitos");
            isValid = false;
        }

        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            etPassword.setError("La contraseña es requerida");
            isValid = false;
        } else if (etPassword.getText().toString().length() < 6) {
            etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            isValid = false;
        }

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
        RegisterRequest request = new RegisterRequest(
                etName.getText().toString().trim(),
                etEmail.getText().toString().trim(),
                etPhone.getText().toString().trim(),
                etPassword.getText().toString(),
                etConfirmPassword.getText().toString()
        );

        AuthApi api = RetrofitClient.getClient().create(AuthApi.class);

        api.register(request).enqueue(new Callback<AuthenticationResponse>() {
            @Override
            public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    AuthenticationResponse authResponse = response.body();
                    String token = authResponse.getToken();


                    SessionManager sessionManager = SessionManager.getInstance(RegisterActivity.this);
                    sessionManager.saveSession(
                            token,
                            null,
                            etName.getText().toString().trim(),
                            etEmail.getText().toString().trim()
                    );

                    String userName = etName.getText().toString().trim();
                    String userEmail = etEmail.getText().toString().trim();

                    Toast.makeText(
                            RegisterActivity.this,
                            "¡Registro exitoso! Completa tu perfil",
                            Toast.LENGTH_LONG
                    ).show();


                    Intent intent = new Intent(RegisterActivity.this, OnboardingActivity.class);
                    intent.putExtra("user_name", userName);
                    intent.putExtra("user_email", userEmail);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();

                } else {
                    String errorMsg = "Error: " + response.code();

                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(
                            RegisterActivity.this,
                            errorMsg,
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                Toast.makeText(
                        RegisterActivity.this,
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();

                t.printStackTrace();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}