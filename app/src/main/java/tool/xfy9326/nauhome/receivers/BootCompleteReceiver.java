package tool.xfy9326.nauhome.receivers;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import tool.xfy9326.nauhome.methods.BaseMethod;
import tool.xfy9326.nauhome.services.WifiListenerNotificationService;

public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            if (BaseMethod.isNotificationListenerServiceEnabled(context)) {
                PackageManager packageManager = context.getPackageManager();
                packageManager.setComponentEnabledSetting(new ComponentName(context, WifiListenerNotificationService.class),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                packageManager.setComponentEnabledSetting(new ComponentName(context, WifiListenerNotificationService.class),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            }
        }
    }
}
