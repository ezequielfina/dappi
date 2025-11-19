package data.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

public class NetworkManager {
    
    private static final String TAG = "NetworkManager";
    private static NetworkManager instance;
    
    private final ConnectivityManager connectivityManager;
    private boolean isConnected = false;
    private NetworkCallback networkCallback;
    
    private NetworkManager(Context context) {
        connectivityManager = (ConnectivityManager) 
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        checkCurrentConnection();
        registerNetworkCallback();
    }
    
    public static synchronized NetworkManager getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkManager(context.getApplicationContext());
        }
        return instance;
    }
    
    private void checkCurrentConnection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities capabilities = 
                        connectivityManager.getNetworkCapabilities(network);
                if (capabilities != null) {
                    isConnected = capabilities.hasCapability(
                            NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                            capabilities.hasCapability(
                                    NetworkCapabilities.NET_CAPABILITY_VALIDATED);
                }
            }
        } else {
            android.net.NetworkInfo networkInfo = 
                    connectivityManager.getActiveNetworkInfo();
            isConnected = networkInfo != null && networkInfo.isConnected();
        }
        Log.d(TAG, "Estado inicial de conexión: " + isConnected);
    }
    
    private void registerNetworkCallback() {
        try {
            NetworkRequest networkRequest = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();
            
            connectivityManager.registerNetworkCallback(
                    networkRequest, 
                    new ConnectivityManager.NetworkCallback() {
                        @Override
                        public void onAvailable(@NonNull Network network) {
                            isConnected = true;
                            Log.d(TAG, "🟢 Conexión disponible");
                            if (networkCallback != null) {
                                networkCallback.onConnected();
                            }
                        }
                        
                        @Override
                        public void onLost(@NonNull Network network) {
                            isConnected = false;
                            Log.d(TAG, "🔴 Conexión perdida");
                            if (networkCallback != null) {
                                networkCallback.onDisconnected();
                            }
                        }
                    }
            );
        } catch (Exception e) {
            Log.e(TAG, "Error al registrar network callback", e);
        }
    }
    
    public boolean isConnected() {
        return isConnected;
    }
    
    public void setNetworkCallback(NetworkCallback callback) {
        this.networkCallback = callback;
    }
    
    public interface NetworkCallback {
        void onConnected();
        void onDisconnected();
    }
}

