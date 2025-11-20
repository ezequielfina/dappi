package data.repository;

import android.content.Context;
import android.util.Log;

import java.io.File;

import data.api.RetrofitClient;
import data.api.UserApi;
import data.database.AppDatabase;
import data.dto.UpdateProfileRequest;
import data.dto.UserProfileResponse;
import data.network.NetworkManager;
import data.repository.callback.UserCallback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private static final String TAG = "UserRepository";
    private static UserRepository instance;

    private final AppDatabase database;
    private final NetworkManager networkManager;
    private final Context context;
    private String authToken;

    private UserRepository(Context context) {
        this.context = context.getApplicationContext();
        this.database = AppDatabase.getInstance(this.context);
        this.networkManager = NetworkManager.getInstance(this.context);
    }

    public static synchronized UserRepository getInstance(Context context) {
        if (instance == null) {
            instance = new UserRepository(context);
        }
        return instance;
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    // --- Método para obtener los datos del usuario ---

    /**
     * Obtiene los datos del usuario autenticado del backend.
     * @param callback Interfaz para devolver el resultado al ViewModel/UI.
     */
    public void fetchUserProfile(UserCallback callback) {
        if (authToken == null) {
            callback.onError("No hay token de autenticación disponible.");
            return;
        }

        if (!networkManager.isConnected()) {
            // Podrías implementar lógica para usar datos de caché aquí
            callback.onError("Sin conexión a Internet.");
            return;
        }

        Log.d(TAG, "🌐 Intentando obtener perfil del usuario.");

        // 1. Obtener la interfaz de la API con el token
        // Asumo que RetrofitClient.getUserApi(authToken) usa el token para headers.
        UserApi api = RetrofitClient.getUserApi(authToken);

        // 2. Crear el objeto Call
        Call<UserProfileResponse> call = api.getMyProfile();

        // 3. Ejecutar la llamada ASÍNCRONA (enqueue) y definir el Callback de Retrofit
        call.enqueue(new Callback<UserProfileResponse>() {

            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                // Manejo de la respuesta HTTP
                if (response.isSuccessful() && response.body() != null) {
                    // Éxito (código 200-300): Devolver datos remotos
                    Log.d(TAG, "✅ Perfil de usuario obtenido con éxito.");
                    callback.onSuccess(response.body());
                } else if (response.code() == 401) {
                    // Manejar Token Expirado/No Autorizado
                    Log.w(TAG, "⚠️ Error 401: Token de autenticación inválido o expirado.");
                    callback.onError("Sesión expirada. Por favor, inicie sesión de nuevo.");
                } else {
                    // Otros errores HTTP (ej. 404, 500)
                    String errorMessage = "Error al obtener perfil: Código " + response.code();
                    Log.e(TAG, "❌ " + errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                // Manejo de errores de red o conversión
                Log.e(TAG, "❌ Error de red al obtener perfil: " + t.getMessage());
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }

    public void updateUserProfile(String newName, String newEmail, String newPassword, UserCallback callback) {
        if (authToken == null || !networkManager.isConnected()) {
            callback.onError("Sin conexión o sesión inválida");
            return;
        }

        UserApi api = RetrofitClient.getUserApi(authToken);

        // Preparamos el request
        String passwordToSend = (newPassword != null && !newPassword.isEmpty()) ? newPassword : null;
        UpdateProfileRequest request = new UpdateProfileRequest(newName, newEmail, passwordToSend);

        // LLAMADA CORREGIDA: Usamos Call<Void>
        api.updateProfile(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // ✅ ÉXITO: El backend dijo "OK" (200), pero no mandó cuerpo (Void).

                    // Truco: Como el backend no nos devuelve el usuario actualizado,
                    // creamos uno "manual" con los datos nuevos para actualizar la UI inmediatamente.
                    UserProfileResponse localUpdate = new UserProfileResponse();
                    localUpdate.setUserName(newName);
                    localUpdate.setEmail(newEmail);
                    // Mantenemos los datos viejos que no cambiaron (opcional, o pedimos fetch de nuevo)
                    // Nota: La foto y stats no cambian en este endpoint.

                    callback.onSuccess(localUpdate);

                } else {
                    callback.onError("Error al actualizar: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }

    /**
     * Sube una nueva foto de perfil.
     * @param photoFile El archivo de imagen obtenido de la galería/cámara.
     */
    public void uploadProfilePicture(File photoFile, UserCallback callback) {
        if (!isValidSession(callback)) return;
        if (photoFile == null || !photoFile.exists()) {
            callback.onError("El archivo de imagen no existe");
            return;
        }

        UserApi api = RetrofitClient.getUserApi(authToken);

        // 1. Crear el RequestBody para el archivo
        // Usamos "image/*" para aceptar jpg, png, etc.
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), photoFile);

        // 2. Crear el MultipartBody.Part
        // "file" es el nombre del parámetro que espera el Backend (@RequestParam("file") MultipartFile file)
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", photoFile.getName(), requestFile);

        // 3. Ejecutar llamada
        api.uploadProfilePicture(body).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error al subir imagen: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                callback.onError("Fallo al subir imagen: " + t.getMessage());
            }
        });
    }

    // Método auxiliar para validar sesión
    private boolean isValidSession(UserCallback callback) {
        if (authToken == null || !networkManager.isConnected()) {
            callback.onError("Sin conexión o sesión inválida");
            return false;
        }
        return true;
    }
}
