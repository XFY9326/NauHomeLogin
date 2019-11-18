package tool.xfy9326.nauhome.methods;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import androidx.preference.PreferenceManager;

import tool.xfy9326.nauhome.Config;

public class NetMethod {

    public static boolean connectCorrectWifiWithIp(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.contains(Config.PREFERENCE_WIFI_SSID)) {
            String ssid = NetMethod.getConnectedWifiName(context);
            String ip = getConnectedWifiIp(context);
            return ssid != null && ssid.equals(sharedPreferences.getString(Config.PREFERENCE_WIFI_SSID, null)) &&
                    ip != null && !ip.equals("0.0.0.0") && !ip.equals("127.0.0.1");
        }
        return false;
    }

    private static String getConnectedWifiName(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            String ssid = wifiManager.getConnectionInfo().getSSID().trim();
            if (ssid.length() >= 2) {
                ssid = ssid.substring(1, ssid.length() - 1);
            }
            return ssid.trim();
        }
        return null;
    }

    public static String getConnectedWifiIp(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo info = wifiManager.getConnectionInfo();
            if (info != null) {
                int paramInt = info.getIpAddress();
                return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "." + (0xFF & paramInt >> 24);
            }
        }
        return null;
    }
}