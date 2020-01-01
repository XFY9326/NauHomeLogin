package tool.xfy9326.nauhome.services;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.service.notification.NotificationListenerService;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import tool.xfy9326.nauhome.Config;
import tool.xfy9326.nauhome.R;
import tool.xfy9326.nauhome.methods.ListenerMethod;
import tool.xfy9326.nauhome.methods.PermissionMethod;

public class WifiListenerNotificationService extends NotificationListenerService {
    private ConnectivityManager.NetworkCallback networkCallback;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("WifiListenerNotificationService", "Start Running");
        initListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("WifiListenerNotificationService", "Stop Running");
        networkCallback = ListenerMethod.stopWifiListener(this, networkCallback);
    }

    private void initListener() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getInt(Config.PREFERENCE_CHOSEN_WIFI_LISTENER, -1) != Config.WIFI_LISTENER_TYPE.NOTIFICATION_SERVICE.ordinal()) {
            Toast.makeText(this, R.string.not_enabled_listener, Toast.LENGTH_LONG).show();
        } else if (!PermissionMethod.hasPermission(this)) {
            Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
        } else if (networkCallback == null) {
            Log.d("WifiListenerNotificationService", "NetworkCallback Init");
            networkCallback = ListenerMethod.startWifiListener(this);
        }
    }
}