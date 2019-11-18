package tool.xfy9326.nauhome.receivers;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.preference.PreferenceManager;

import tool.xfy9326.nauhome.Config;
import tool.xfy9326.nauhome.services.WifiListenerNotificationService;

public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (sharedPreferences.getInt(Config.PREFERENCE_CHOSEN_WIFI_LISTENER, -1) == Config.WIFI_LISTENER_TYPE.NOTIFICATION_SERVICE.ordinal()) {
                PackageManager packageManager = context.getPackageManager();
                packageManager.setComponentEnabledSetting(new ComponentName(context, WifiListenerNotificationService.class),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                packageManager.setComponentEnabledSetting(new ComponentName(context, WifiListenerNotificationService.class),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            }
        }
    }
}
