package tool.xfy9326.nauhome.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import tool.xfy9326.nauhome.Config;
import tool.xfy9326.nauhome.R;
import tool.xfy9326.nauhome.methods.ListenerMethod;
import tool.xfy9326.nauhome.methods.NotificationMethod;
import tool.xfy9326.nauhome.methods.PermissionMethod;

public class LocalListenerService extends Service {
    public static final String COMMAND_CLOSE = "COMMAND_CLOSE";
    private ConnectivityManager.NetworkCallback networkCallback;

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(NotificationMethod.NOTIFICATION_CODE_FOREGROUND_LISTENER, NotificationMethod.getForegroundNotification(this));
        initListener();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        networkCallback = ListenerMethod.stopWifiListener(this, networkCallback);
        stopForeground(true);
        super.onDestroy();
    }

    private void finishService() {
        stopForeground(true);
        stopSelf();
    }

    private void initListener() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getInt(Config.PREFERENCE_CHOSEN_WIFI_LISTENER, -1) != Config.WIFI_LISTENER_TYPE.LOCAL_SERVICE.ordinal()) {
            Toast.makeText(this, R.string.not_enabled_listener, Toast.LENGTH_LONG).show();
            finishService();
        } else if (!PermissionMethod.hasPermission(this)) {
            Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            finishService();
        } else if (networkCallback == null) {
            networkCallback = ListenerMethod.startWifiListener(this);
        }
    }
}
