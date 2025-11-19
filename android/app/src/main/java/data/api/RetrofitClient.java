package data.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {

    // Configuración para emulador (10.0.2.2) o dispositivo físico (IP de tu PC)
    private static final String BASE_URL = "http://192.168.0.13:8080/api/v1/";
    // private static final String BASE_URL = "http://192.168.0.13:8080/api/v1/";

    private static Retrofit retrofit = null;
    private static Retrofit retrofitWithAuth = null;

    /**
     * Cliente básico sin autenticación
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(getLoggingInterceptor())
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    /**
     * Cliente con autenticación (Bearer Token)
     * IMPORTANTE: NO cachear - crear nuevo cliente cada vez con token actualizado
     */
    public static Retrofit getClientWithAuth(String token) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new AuthInterceptor(token))
                .addInterceptor(getLoggingInterceptor())
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Logging interceptor para debug
     */
    private static HttpLoggingInterceptor getLoggingInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return logging;
    }

    // ===== Factory Methods para APIs =====

    public static AuthApi getAuthApi() {
        return getClient().create(AuthApi.class);
    }

    public static UserApi getUserApi(String token) {
        return getClientWithAuth(token).create(UserApi.class);
    }

    public static PlaceApi getPlaceApi(String token) {
        return getClientWithAuth(token).create(PlaceApi.class);
    }

    public static ReviewApi getReviewApi(String token) {
        return getClientWithAuth(token).create(ReviewApi.class);
    }
}