package ar.edu.uade.api.ui.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

public class ImageUtils {
    
    private static final String TAG = "ImageUtils";
    
    /**
     * Detecta si una URL es Base64 y la decodifica, o la retorna tal cual si es URL HTTP.
     * 
     * Formatos soportados:
     * - Base64: JPEG, PNG, GIF, WebP, BMP
     * - HTTP/HTTPS: Cualquier URL válida
     * 
     * @param imageUrl URL de la imagen o string Base64
     * @return Bitmap si es Base64, String si es URL, null si hay error
     */
    public static Object getImageSource(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            Log.w(TAG, "⚠️ Image URL is null or empty");
            return null;
        }
        
        // Detectar si es Base64 (formato: data:image/[formato];base64,[datos])
        if (imageUrl.startsWith("data:image")) {
            return decodeBase64Image(imageUrl);
        } else if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            // URL HTTP/HTTPS normal
            Log.d(TAG, "🌐 Loading HTTP/HTTPS URL: " + imageUrl.substring(0, Math.min(60, imageUrl.length())) + "...");
            return imageUrl;
        } else if (imageUrl.startsWith("/") || imageUrl.startsWith("file://")) {
            // Ruta de archivo local
            Log.d(TAG, "📁 Loading local file: " + imageUrl);
            return imageUrl;
        } else {
            // Formato desconocido, intentar como URL
            Log.w(TAG, "⚠️ Unknown format, treating as URL: " + imageUrl.substring(0, Math.min(50, imageUrl.length())));
            return imageUrl;
        }
    }
    
    /**
     * Decodifica una imagen Base64 a Bitmap
     */
    private static Bitmap decodeBase64Image(String base64String) {
        try {
            // Extraer el formato de imagen (jpeg, png, gif, webp, etc.)
            String format = extractImageFormat(base64String);
            Log.d(TAG, "📷 Detected Base64 image format: " + format);
            
            // Validar que tenga la coma separadora
            if (!base64String.contains(",")) {
                Log.e(TAG, "❌ Invalid Base64 format: missing comma separator");
                return null;
            }
            
            // Extraer solo la parte Base64 (después de la coma)
            String pureBase64 = base64String.substring(base64String.indexOf(",") + 1);
            
            // Validar que no esté vacío
            if (pureBase64.isEmpty()) {
                Log.e(TAG, "❌ Base64 data is empty");
                return null;
            }
            
            // Decodificar Base64 a byte array
            byte[] decodedBytes = Base64.decode(pureBase64, Base64.DEFAULT);
            
            // Validar tamaño de los bytes decodificados
            Log.d(TAG, "📦 Decoded bytes size: " + decodedBytes.length + " bytes");
            
            // Convertir byte array a Bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            
            if (bitmap != null) {
                Log.d(TAG, "✅ Base64 image decoded successfully: " + bitmap.getWidth() + "x" + bitmap.getHeight() + " px");
                return bitmap;
            } else {
                Log.e(TAG, "❌ BitmapFactory failed to decode bytes (unsupported format or corrupted data)");
                return null;
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "❌ Invalid Base64 string: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            Log.e(TAG, "❌ Error decoding Base64 image: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Extrae el formato de imagen de un string Base64
     * Ejemplo: "data:image/jpeg;base64,..." -> "jpeg"
     */
    private static String extractImageFormat(String base64String) {
        try {
            if (base64String.startsWith("data:image/")) {
                String formatPart = base64String.substring(11); // después de "data:image/"
                int semicolonIndex = formatPart.indexOf(";");
                if (semicolonIndex > 0) {
                    return formatPart.substring(0, semicolonIndex);
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Could not extract format: " + e.getMessage());
        }
        return "unknown";
    }
    
    /**
     * Verifica si una URL es Base64
     */
    public static boolean isBase64Image(String imageUrl) {
        return imageUrl != null && imageUrl.startsWith("data:image");
    }
    
    /**
     * Obtiene información sobre el formato de la imagen
     */
    public static String getImageFormat(String imageUrl) {
        if (imageUrl == null) return "null";
        if (imageUrl.startsWith("data:image")) {
            return "Base64 (" + extractImageFormat(imageUrl) + ")";
        } else if (imageUrl.startsWith("http")) {
            return "HTTP/HTTPS URL";
        } else if (imageUrl.startsWith("file://") || imageUrl.startsWith("/")) {
            return "Local file";
        }
        return "Unknown";
    }
}

