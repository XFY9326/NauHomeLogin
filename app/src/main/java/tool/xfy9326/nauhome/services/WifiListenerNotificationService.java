package tool.xfy9326.nauhome.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.service.notification.NotificationListenerService;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import tool.xfy9326.nauhome.Config;
import tool.xfy9326.nauhome.R;
import tool.xfy9326.nauhome.methods.PermissionMethod;

public class WifiListenerNotificationService extends NotificationListenerService {
    private ConnectivityManager.NetworkCallback networkCallback;

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getInt(Config.PREFERENCE_CHOSEN_WIFI_LISTENER, -1) != Config.WIFI_LISTENER_TYPE.NOTIFICATION_SERVICE.ordinal()) {
            Toast.makeText(this, R.string.not_enabled_listener, Toast.LENGTH_LONG).show();
        } else if (!PermissionMethod.hasPermission(this)) {
            Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
        } else {
            startWifiListener();
        }
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        stopWifiListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopWifiListener();
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
        startService(new Intent(WifiListenerNotificationService.this, LoginService.class)
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
