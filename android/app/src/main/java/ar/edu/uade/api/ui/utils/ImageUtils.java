package ar.edu.uade.api.ui.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

public class ImageUtils {
    
    private static final String TAG = "ImageUtils";
    
    /**
     * Detecta si una URL es Base64 y la decodifica, o la retorna tal cual si es URL HTTP
     */
    public static Object getImageSource(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        
        // Detectar si es Base64
        if (imageUrl.startsWith("data:image")) {
            try {
                // Extraer la parte Base64 (después de "data:image/jpeg;base64,")
                String base64String = imageUrl.substring(imageUrl.indexOf(",") + 1);
                
                // Decodificar Base64 a byte array
                byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
                
                // Convertir a Bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                
                if (bitmap != null) {
                    Log.d(TAG, "✅ Base64 image decoded successfully");
                    return bitmap;
                } else {
                    Log.e(TAG, "❌ Failed to decode Base64 to Bitmap");
                    return null;
                }
            } catch (Exception e) {
                Log.e(TAG, "❌ Error decoding Base64 image: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        } else {
            // Es una URL HTTP normal
            Log.d(TAG, "Loading HTTP URL: " + imageUrl);
            return imageUrl;
        }
    }
    
    /**
     * Verifica si una URL es Base64
     */
    public static boolean isBase64Image(String imageUrl) {
        return imageUrl != null && imageUrl.startsWith("data:image");
    }
}

