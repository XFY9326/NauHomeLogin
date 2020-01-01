package tool.xfy9326.nauhome.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.util.Log;

import androidx.preference.PreferenceManager;

import tool.xfy9326.nauhome.Config;
import tool.xfy9326.nauhome.methods.ListenerMethod;
import tool.xfy9326.nauhome.methods.NetMethod;
import tool.xfy9326.nauhome.methods.PermissionMethod;

public class CaptivePortalReceiver extends BroadcastReceiver {
    private static final String ACTION_MIUI_PORTAL_LOGIN = "com.miui.action.OPEN_WIFI_LOGIN";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null && (intent.getAction().equals(ACTION_MIUI_PORTAL_LOGIN) || intent.getAction().equals(ConnectivityManager.ACTION_CAPTIVE_PORTAL_SIGN_IN))) {
            Log.d("CaptivePortalReceiver", "Receive Captive Portal Broadcast");
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (sharedPreferences.getInt(Config.PREFERENCE_CHOSEN_WIFI_LISTENER, -1) == Config.WIFI_LISTENER_TYPE.MIUI_SPECIAL_SUPPORT.ordinal()) {
                if (PermissionMethod.hasPermission(context)) {
                    if (NetMethod.connectCorrectWifiWithIp(context)) {
                        Log.d("CaptivePortalReceiver", "Start Login Service");
                        ListenerMethod.startLoginService(context);
                    }
                }
            }
        }
    }
}