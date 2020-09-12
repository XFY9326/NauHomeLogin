package tool.xfy9326.nauhome;

import android.app.Application;

import tool.xfy9326.nauhome.methods.NotificationMethod;

public class App extends Application {
    public static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        NotificationMethod.createNotifyNotificationChannel(this);
        NotificationMethod.createForegroundNotificationChannel(this);
    }
}
