package tool.xfy9326.nauhome.services;

import android.accessibilityservice.AccessibilityService;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import tool.xfy9326.nauhome.Config;
import tool.xfy9326.nauhome.R;
import tool.xfy9326.nauhome.methods.ListenerMethod;
import tool.xfy9326.nauhome.methods.NotificationMethod;
import tool.xfy9326.nauhome.methods.PermissionMethod;

public class WifiListenerAccessibilityService extends AccessibilityService {
    private ConnectivityManager.NetworkCallback networkCallback;
    private boolean enableForegroundService = true;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d("WifiListenerAccessibilityService", "Service Connected");
        enableForegroundService = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Config.PREFERENCE_ENABLE_FOREGROUND_SERVICE, true);
        if (enableForegroundService) {
            startForeground(NotificationMethod.NOTIFICATION_CODE_FOREGROUND, NotificationMethod.getForegroundNotification(this));
        }
        initListener();
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public void onDestroy() {
        Log.d("WifiListenerAccessibilityService", "Stop Running");
        networkCallback = ListenerMethod.stopWifiListener(this, networkCallback);
        if (enableForegroundService) {
            stopForeground(true);
        }
        super.onDestroy();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    private void finishService() {
        if (enableForegroundService) {
            stopForeground(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            disableSelf();
        }
        stopSelf();
    }

    private void initListener() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getInt(Config.PREFERENCE_CHOSEN_WIFI_LISTENER, -1) != Config.WIFI_LISTENER_TYPE.ACCESSIBILITY_SERVICE.ordinal()) {
            Toast.makeText(this, R.string.not_enabled_listener, Toast.LENGTH_LONG).show();
            finishService();
        } else if (!PermissionMethod.hasPermission(this)) {
            Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            finishService();
        } else if (networkCallback == null) {
            Log.d("WifiListenerAccessibilityService", "NetworkCallback Init");
            networkCallback = ListenerMethod.startWifiListener(this);
        }
    }
}
