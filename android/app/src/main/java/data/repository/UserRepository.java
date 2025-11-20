package data.repository;

import android.content.Context;
import android.util.Log;

import data.api.RetrofitClient;
import data.api.UserApi;
import data.database.AppDatabase;
import data.dto.UserProfileResponse;
import data.network.NetworkManager;
import data.repository.callback.UserCallback;
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
}
