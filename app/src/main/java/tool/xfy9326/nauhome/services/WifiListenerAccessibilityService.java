package tool.xfy9326.nauhome.services;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import tool.xfy9326.nauhome.Config;
import tool.xfy9326.nauhome.R;
import tool.xfy9326.nauhome.methods.PermissionMethod;

public class WifiListenerAccessibilityService extends AccessibilityService {
    private ConnectivityManager.NetworkCallback networkCallback;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getInt(Config.PREFERENCE_CHOSEN_WIFI_LISTENER, -1) != Config.WIFI_LISTENER_TYPE.ACCESSIBILITY_SERVICE.ordinal()) {
            Toast.makeText(this, R.string.not_enabled_listener, Toast.LENGTH_LONG).show();
            finishService();
        } else if (!PermissionMethod.hasPermission(this)) {
            Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            finishService();
        } else {
            startWifiListener();
        }
    }

    private void finishService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            disableSelf();
        }
        stopSelf();
    }

    @Override
    public void onInterrupt() {
        stopWifiListener();
    }

    @Override
    public void onDestroy() {
        stopWifiListener();
        super.onDestroy();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    private void startWifiListener() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkRequest.Builder builder = new NetworkRequest.Builder();

            NetworkRequest request = builder.addCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL)
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .build();
            networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                    if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL) &&
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        startLoginService();
                    }
                    super.onCapabilitiesChanged(network, networkCapabilities);
                }
            };
            connectivityManager.registerNetworkCallback(request, networkCallback);
        }
    }

    private void startLoginService() {
        startService(new Intent(WifiListenerAccessibilityService.this, LoginService.class)
                .putExtra(LoginService.OPERATION_TAG, LoginService.OPERATION_LOGIN)
                .putExtra(LoginService.REPORT_TAG, LoginService.REPORT_NOTIFICATION));
    }

    private void stopWifiListener() {
        if (networkCallback != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                connectivityManager.unregisterNetworkCallback(networkCallback);
                networkCallback = null;
            }
        }
    }
}
