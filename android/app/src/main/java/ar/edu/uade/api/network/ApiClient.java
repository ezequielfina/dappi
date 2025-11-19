package ar.edu.uade.api.network;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApiClient {
    
    private static final String TAG = "ApiClient";
    private static final String BASE_URL = "http://10.0.2.2:8080"; // Para emulador Android
    // private static final String BASE_URL = "http://localhost:8080"; // Para dispositivo físico, usar IP de tu PC
    
    private final ExecutorService executorService;
    
    public ApiClient() {
        this.executorService = Executors.newFixedThreadPool(4);
    }
    
    /**
     * Envía una reseña con foto al backend
     */
    public void sendReviewWithPhoto(long userId, long placeId, String description, 
                                   int rating, double latitude, double longitude,
                                   File photoFile, ApiCallback callback) {
        executorService.execute(() -> {
            try {
                String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
                String urlString = BASE_URL + "/api/v1/reviews/with-photos";
                
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                
                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                
                // Agregar JSON de la reseña
                JSONObject reviewJson = new JSONObject();
                reviewJson.put("userId", userId);
                reviewJson.put("placeId", placeId);
                reviewJson.put("description", description);
                reviewJson.put("rateToPlace", rating);
                reviewJson.put("userLatitude", latitude);
                reviewJson.put("userLongitude", longitude);
                
                outputStream.writeBytes("--" + boundary + "\r\n");
                outputStream.writeBytes("Content-Disposition: form-data; name=\"review\"\r\n");
                outputStream.writeBytes("Content-Type: application/json\r\n\r\n");
                outputStream.writeBytes(reviewJson.toString());
                outputStream.writeBytes("\r\n");
                
                // Agregar foto si existe
                if (photoFile != null && photoFile.exists()) {
                    outputStream.writeBytes("--" + boundary + "\r\n");
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"photos\"; filename=\"" + 
                            photoFile.getName() + "\"\r\n");
                    outputStream.writeBytes("Content-Type: image/jpeg\r\n\r\n");
                    
                    FileInputStream fileInputStream = new FileInputStream(photoFile);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    fileInputStream.close();
                    outputStream.writeBytes("\r\n");
                }
                
                outputStream.writeBytes("--" + boundary + "--\r\n");
                outputStream.flush();
                outputStream.close();
                
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    Log.d(TAG, "Response: " + response.toString());
                    callback.onSuccess(response.toString());
                } else {
                    BufferedReader errorReader = new BufferedReader(
                            new InputStreamReader(connection.getErrorStream()));
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    errorReader.close();
                    
                    Log.e(TAG, "Error Response: " + errorResponse.toString());
                    callback.onError("Error " + responseCode + ": " + errorResponse.toString());
                }
                
                connection.disconnect();
                
            } catch (Exception e) {
                Log.e(TAG, "Error al enviar reseña", e);
                callback.onError("Error de conexión: " + e.getMessage());
            }
        });
    }
    
    /**
     * Interface para callbacks de la API
     */
    public interface ApiCallback {
        void onSuccess(String response);
        void onError(String error);
    }
}

