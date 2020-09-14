package tool.xfy9326.nauhome;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import tool.xfy9326.nauhome.methods.ListenerMethod;
import tool.xfy9326.nauhome.methods.NotificationMethod;

public class App extends Application {
    public static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        NotificationMethod.createNotifyNotificationChannel(this);
        NotificationMethod.createForegroundNotificationChannel(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getInt(Config.PREFERENCE_CHOSEN_WIFI_LISTENER, -1) == Config.WIFI_LISTENER_TYPE.LOCAL_SERVICE.ordinal()) {
            ListenerMethod.startLocalListenerService(this);
        }
    }
}
