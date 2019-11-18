package tool.xfy9326.nauhome.methods;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.NotificationManagerCompat;

import java.util.Set;

public class BaseMethod {
    public static boolean isMIUI() {
        return Build.MANUFACTURER.equals("Xiaomi") && Build.HOST.contains("miui");
    }

    public static boolean isAccessibilityServiceEnabled(Context context) {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (accessibilityEnabled == 1) {
            String services = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (services != null) {
                return services.toLowerCase().contains(context.getPackageName().toLowerCase());
            }
        }
        return false;
    }

    public static boolean isNotificationListenerServiceEnabled(Context context) {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(context);
        return packageNames.contains(context.getPackageName());
    }
}
