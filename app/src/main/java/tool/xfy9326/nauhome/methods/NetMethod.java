package tool.xfy9326.nauhome.methods;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import androidx.preference.PreferenceManager;

import tool.xfy9326.nauhome.Config;

public class NetMethod {
    private static final String NAU_HOME_LOGIN_HOST = "10.255.252.20";
    private static final String UNKNOWN_SSID = "unknown ssid";

    public static boolean needCaptivePortalLogin(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (networkCapabilities != null) {
                return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL) && networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
            }
        }
        return false;
    }

    public static boolean connectCorrectIp(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.contains(Config.PREFERENCE_WIFI_SSID)) {
            String ip = getConnectedWifiIp(context);
            return ip != null && !ip.equals("0.0.0.0") && !ip.equals("127.0.0.1");
        }
        return false;
    }

    public static boolean connectCorrectWifi(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.contains(Config.PREFERENCE_WIFI_SSID)) {
            String ssid = NetMethod.getConnectedWifiName(context);
            if (ssid == null || ssid.contains(UNKNOWN_SSID)) {
                return judgeCaptivePortalConnect();
            } else {
                String saved_ssid = sharedPreferences.getString(Config.PREFERENCE_WIFI_SSID, null);
                return saved_ssid != null && ssid.contains(saved_ssid);
            }
        }
        return false;
    }

    private static String getConnectedWifiName(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            String ssid = wifiManager.getConnectionInfo().getSSID().trim();
            if (ssid.length() >= 2 && ssid.startsWith("\"") && ssid.endsWith("\"")) {
                ssid = ssid.substring(1, ssid.length() - 1).trim();
            }
            return ssid;
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

    public static void requestReevaluateNetwork(Context context, boolean hasConnection) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.reportNetworkConnectivity(null, hasConnection);
    }

    private static boolean judgeCaptivePortalConnect() {
        try {
            Process p = Runtime.getRuntime().exec("ping -c 2 -w 5 " + NetMethod.NAU_HOME_LOGIN_HOST);
            int status = p.waitFor();
            return status == 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
