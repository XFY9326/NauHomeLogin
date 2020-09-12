package tool.xfy9326.nauhome.methods;

import android.content.SharedPreferences;
import android.os.Looper;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import tool.xfy9326.nauhome.App;
import tool.xfy9326.nauhome.Config;
import tool.xfy9326.nauhome.R;

public class LoginInstance {
    public static final int REPORT_NOTIFICATION = 1;
    public static final int REPORT_TOAST = 2;

    private static LoginInstance instance;
    private final SharedPreferences sharedPreferences;
    private boolean justLogout = false;


    private LoginInstance() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.instance);
    }

    public synchronized static LoginInstance getInstance() {
        if (instance == null) {
            instance = new LoginInstance();
        }
        return instance;
    }

    public void login(final int reportType) {
        if (justLogout) {
            justLogout = false;
            return;
        }
        String ip = getIP();
        if (ip != null) {
            if (sharedPreferences.contains(Config.PREFERENCE_LOGIN_TYPE) && sharedPreferences.contains(Config.PREFERENCE_LOGIN_ID) && sharedPreferences.contains(Config.PREFERENCE_PASSWORD)) {
                CaptivePortalLoginMethod.login(ip, sharedPreferences.getString(Config.PREFERENCE_LOGIN_ID, null),
                        sharedPreferences.getString(Config.PREFERENCE_PASSWORD, null),
                        sharedPreferences.getString(Config.PREFERENCE_LOGIN_TYPE, null), new CaptivePortalLoginMethod.OnRequestListener() {
                            @Override
                            public void OnRequest(boolean isSuccess, String errorMsg) {
                                if (isSuccess) {
                                    NetMethod.requestReevaluateNetwork(App.instance, true);
                                    report(reportType, App.instance.getString(R.string.login_success));
                                } else {
                                    report(reportType, App.instance.getString(R.string.login_failed, errorMsg));
                                }
                            }
                        });
            }
        }
    }

    public void logout(final int reportType) {
        String ip = getIP();
        if (ip != null) {
            if (sharedPreferences.contains(Config.PREFERENCE_LOGIN_ID) && sharedPreferences.contains(Config.PREFERENCE_PASSWORD)) {
                CaptivePortalLoginMethod.logout(ip, sharedPreferences.getString(Config.PREFERENCE_LOGIN_ID, null),
                        sharedPreferences.getString(Config.PREFERENCE_PASSWORD, null), new CaptivePortalLoginMethod.OnRequestListener() {
                            @Override
                            public void OnRequest(boolean isSuccess, String errorMsg) {
                                if (isSuccess) {
                                    justLogout = true;
                                    NetMethod.requestReevaluateNetwork(App.instance, false);
                                    report(reportType, App.instance.getString(R.string.logout_success));
                                } else {
                                    report(reportType, App.instance.getString(R.string.logout_failed, errorMsg));
                                }
                            }
                        });
            }
        }
    }

    private String getIP() {
        if (NetMethod.connectCorrectWifi(App.instance)) {
            return NetMethod.getConnectedWifiIp(App.instance);
        }
        return null;
    }

    private void report(int reportType, String msg) {
        if (NetMethod.connectCorrectWifi(App.instance)) {
            if (NetMethod.connectCorrectIp(App.instance)) {
                if (reportType == REPORT_NOTIFICATION) {
                    NotificationMethod.reportLoginResult(App.instance, App.instance.getString(R.string.app_name), msg);
                } else if (reportType == REPORT_TOAST) {
                    Looper.prepare();
                    Toast.makeText(App.instance, msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            } else {
                if (reportType == REPORT_NOTIFICATION) {
                    NotificationMethod.reportLoginResult(App.instance, App.instance.getString(R.string.app_name), App.instance.getString(R.string.login_result_error));
                } else if (reportType == REPORT_TOAST) {
                    Looper.prepare();
                    Toast.makeText(App.instance, R.string.login_result_error, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    }
}
