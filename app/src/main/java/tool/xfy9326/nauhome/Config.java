package tool.xfy9326.nauhome;

public class Config {
    public static final String PREFERENCE_LOGIN_ID = "LOGIN_ID";
    public static final String PREFERENCE_PASSWORD = "PASSWORD";
    public static final String PREFERENCE_LOGIN_TYPE = "LOGIN_TYPE";
    public static final String PREFERENCE_WIFI_SSID = "WIFI_SSID";
    public static final String PREFERENCE_CHOSEN_WIFI_LISTENER = "CHOSEN_WIFI_LISTENER";
    public static final String PREFERENCE_ENABLE_FOREGROUND_SERVICE = "ENABLE_FOREGROUND_SERVICE";

    public enum WIFI_LISTENER_TYPE {
        ACCESSIBILITY_SERVICE,
        NOTIFICATION_SERVICE,
        MIUI_SPECIAL_SUPPORT,
        LOCAL_SERVICE
    }
}
