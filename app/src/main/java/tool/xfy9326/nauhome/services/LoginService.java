package tool.xfy9326.nauhome.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import tool.xfy9326.nauhome.Config;
import tool.xfy9326.nauhome.R;
import tool.xfy9326.nauhome.methods.LoginMethod;
import tool.xfy9326.nauhome.methods.NetMethod;
import tool.xfy9326.nauhome.methods.NotificationMethod;

public class LoginService extends Service {
    public static final String OPERATION_TAG = "OPERATION_TAG";
    public static final int OPERATION_LOGIN = 0;
    public static final int OPERATION_LOGOUT = 1;
    public static final String REPORT_TAG = "REPORT_TAG";
    public static final int REPORT_NOTIFICATION = 2;
    public static final int REPORT_TOAST = 3;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String ip = NetMethod.getConnectedWifiIp(this);
        if (ip != null) {
            if (intent != null) {
                Log.d("LoginService", "Receive Start Service Command");
                int operation = intent.getIntExtra(OPERATION_TAG, -1);
                int report = intent.getIntExtra(REPORT_TAG, -1);
                if (operation == OPERATION_LOGIN) {
                    login(ip, report);
                } else if (operation == OPERATION_LOGOUT) {
                    logout(ip, report);
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void login(String ip, final int reportType) {
        if (sharedPreferences.contains(Config.PREFERENCE_LOGIN_TYPE)
                && sharedPreferences.contains(Config.PREFERENCE_LOGIN_ID)
                && sharedPreferences.contains(Config.PREFERENCE_PASSWORD)) {
            Log.d("LoginService", "Try Login");
            LoginMethod.login(ip, sharedPreferences.getString(Config.PREFERENCE_LOGIN_ID, null),
                    sharedPreferences.getString(Config.PREFERENCE_PASSWORD, null),
                    sharedPreferences.getString(Config.PREFERENCE_LOGIN_TYPE, null), new LoginMethod.OnRequestListener() {
                        @Override
                        public void OnRequest(boolean isSuccess, String errorMsg) {
                            if (isSuccess) {
                                report(reportType, getString(R.string.login_success));
                            } else {
                                report(reportType, getString(R.string.login_failed, errorMsg));
                            }
                        }
                    });
        }
    }

    private void logout(String ip, final int reportType) {
        if (sharedPreferences.contains(Config.PREFERENCE_LOGIN_ID)
                && sharedPreferences.contains(Config.PREFERENCE_PASSWORD)) {
            Log.d("LoginService", "Try Logout");
            LoginMethod.logout(ip, sharedPreferences.getString(Config.PREFERENCE_LOGIN_ID, null),
                    sharedPreferences.getString(Config.PREFERENCE_PASSWORD, null), new LoginMethod.OnRequestListener() {
                        @Override
                        public void OnRequest(boolean isSuccess, String errorMsg) {
                            if (isSuccess) {
                                report(reportType, getString(R.string.logout_success));
                            } else {
                                report(reportType, getString(R.string.logout_failed, errorMsg));
                            }
                        }
                    });
        }
    }

    private void report(int reportType, String msg) {
        Log.d("LoginService", "Report Result");
        if (NetMethod.connectCorrectWifiWithIp(LoginService.this)) {
            if (reportType == REPORT_NOTIFICATION) {
                NotificationMethod.reportLoginResult(LoginService.this, getString(R.string.app_name), msg);
            } else if (reportType == REPORT_TOAST) {
                Looper.prepare();
                Toast.makeText(LoginService.this, msg, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        } else {
            if (reportType == REPORT_NOTIFICATION) {
                NotificationMethod.reportLoginResult(LoginService.this, getString(R.string.app_name), getString(R.string.login_result_error));
            } else if (reportType == REPORT_TOAST) {
                Looper.prepare();
                Toast.makeText(LoginService.this, R.string.login_result_error, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }
    }
}
