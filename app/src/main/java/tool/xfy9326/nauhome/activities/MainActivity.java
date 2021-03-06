package tool.xfy9326.nauhome.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import java.util.Objects;

import tool.xfy9326.nauhome.BuildConfig;
import tool.xfy9326.nauhome.Config;
import tool.xfy9326.nauhome.R;
import tool.xfy9326.nauhome.methods.BaseMethod;
import tool.xfy9326.nauhome.methods.ListenerMethod;
import tool.xfy9326.nauhome.methods.LoginInstance;
import tool.xfy9326.nauhome.methods.NetMethod;
import tool.xfy9326.nauhome.methods.PermissionMethod;

public class MainActivity extends Activity {
    private static final String EMPTY = "";
    private SharedPreferences sharedPreferences;
    private int chosenListenerType = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Objects.requireNonNull(getActionBar()).setTitle(R.string.app_long_name);
        setView();
        setPermissionStatus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_main_about) {
            showAboutDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE))
                .setMessage(R.string.about_application)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void setView() {
        final String[] ssid = getResources().getStringArray(R.array.ssid);
        final String[] login_type = getResources().getStringArray(R.array.login_type);
        final String[] listener = getResources().getStringArray(R.array.wifi_listener_type);

        final EditText loginID = findViewById(R.id.editText_login_id);
        final EditText password = findViewById(R.id.editText_login_password);
        final Spinner loginType = findViewById(R.id.spinner_login_type);

        Button listenerService = findViewById(R.id.button_listener_service);
        listenerService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences.getBoolean(Config.PREFERENCE_ENABLE_FOREGROUND_SERVICE, true) &&
                        !BaseMethod.areNotificationsEnabled(MainActivity.this)) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.attention)
                            .setMessage(R.string.notification_permission_attention)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    BaseMethod.gotoNotificationSettings(MainActivity.this);
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .show();
                } else if (PermissionMethod.hasPermission(MainActivity.this)) {
                    chosenListenerType = sharedPreferences.getInt(Config.PREFERENCE_CHOSEN_WIFI_LISTENER, 0);

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.enable_listener)
                            .setSingleChoiceItems(listener, chosenListenerType, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    chosenListenerType = which;
                                }
                            })
                            .setPositiveButton(R.string.set_it, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    changeWifiListener();
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .show();
                } else {
                    Toast.makeText(MainActivity.this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginID.setText(sharedPreferences.getString(Config.PREFERENCE_LOGIN_ID, EMPTY));
        password.setText(sharedPreferences.getString(Config.PREFERENCE_PASSWORD, EMPTY));
        String savedType = sharedPreferences.getString(Config.PREFERENCE_LOGIN_TYPE, EMPTY);
        for (int i = 0; i < login_type.length; i++) {
            if (login_type[i].equals(savedType)) {
                loginType.setSelection(i);
                break;
            }
        }

        final Button save = findViewById(R.id.button_save);
        Switch foreground = findViewById(R.id.checkbox_foreground_service);
        Switch ssid_unknown = findViewById(R.id.checkbox_ssid_unknown_action);
        Button grant = findViewById(R.id.button_grant_permission);
        Button login = findViewById(R.id.button_login);
        Button logout = findViewById(R.id.button_logout);
        Button gps = findViewById(R.id.button_gps_settings);

        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });

        ssid_unknown.setChecked(sharedPreferences.getBoolean(Config.PREFERENCE_TRY_LOGIN_EVEN_SSID_IS_UNKNOWN, true));
        ssid_unknown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean(Config.PREFERENCE_TRY_LOGIN_EVEN_SSID_IS_UNKNOWN, isChecked).apply();
                if (!isChecked) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.attention)
                            .setMessage(R.string.try_login_when_ssid_unknown_attention)
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
            }
        });

        foreground.setChecked(sharedPreferences.getBoolean(Config.PREFERENCE_ENABLE_FOREGROUND_SERVICE, true));
        foreground.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean(Config.PREFERENCE_ENABLE_FOREGROUND_SERVICE, isChecked).apply();
                Toast.makeText(MainActivity.this, R.string.manually_restart_service, Toast.LENGTH_SHORT).show();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionMethod.hasPermission(MainActivity.this)) {
                    if (NetMethod.needCaptivePortalLogin(MainActivity.this)) {
                        LoginInstance.getInstance().login(LoginInstance.REPORT_TOAST);
                    } else {
                        Toast.makeText(MainActivity.this, R.string.already_logon, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionMethod.hasPermission(MainActivity.this)) {
                    LoginInstance.getInstance().logout(LoginInstance.REPORT_TOAST);
                } else {
                    Toast.makeText(MainActivity.this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int type = loginType.getSelectedItemPosition();
                sharedPreferences.edit()
                        .putString(Config.PREFERENCE_LOGIN_ID, loginID.getText().toString())
                        .putString(Config.PREFERENCE_PASSWORD, password.getText().toString())
                        .putString(Config.PREFERENCE_LOGIN_TYPE, login_type[type])
                        .putString(Config.PREFERENCE_WIFI_SSID, ssid[type])
                        .apply();
                Toast.makeText(MainActivity.this, R.string.save_success, Toast.LENGTH_SHORT).show();
            }
        });

        grant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionMethod.requestLocationPermission(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void changeWifiListener() {
        if (chosenListenerType == Config.WIFI_LISTENER_TYPE.ACCESSIBILITY_SERVICE.ordinal()) {
            if (BaseMethod.isNotificationListenerServiceEnabled(MainActivity.this)) {
                Toast.makeText(MainActivity.this, R.string.need_disable_notification_service, Toast.LENGTH_SHORT).show();
            } else {
                if (sharedPreferences.getInt(Config.PREFERENCE_CHOSEN_WIFI_LISTENER, -1) == Config.WIFI_LISTENER_TYPE.LOCAL_SERVICE.ordinal()) {
                    ListenerMethod.stopLocalListenerService(MainActivity.this);
                }
                sharedPreferences.edit().putInt(Config.PREFERENCE_CHOSEN_WIFI_LISTENER, chosenListenerType).apply();
                try {
                    startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, R.string.start_settings_accessibility_activity_error, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        } else if (chosenListenerType == Config.WIFI_LISTENER_TYPE.NOTIFICATION_SERVICE.ordinal()) {
            if (BaseMethod.isAccessibilityServiceEnabled(MainActivity.this)) {
                Toast.makeText(MainActivity.this, R.string.need_disable_accessibility_service, Toast.LENGTH_SHORT).show();
            } else {
                if (sharedPreferences.getInt(Config.PREFERENCE_CHOSEN_WIFI_LISTENER, -1) == Config.WIFI_LISTENER_TYPE.LOCAL_SERVICE.ordinal()) {
                    ListenerMethod.stopLocalListenerService(MainActivity.this);
                }
                sharedPreferences.edit().putInt(Config.PREFERENCE_CHOSEN_WIFI_LISTENER, chosenListenerType).apply();
                try {
                    startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, R.string.start_settings_notification_activity_error, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        } else if (chosenListenerType == Config.WIFI_LISTENER_TYPE.MIUI_SPECIAL_SUPPORT.ordinal()) {
            if (BaseMethod.isAccessibilityServiceEnabled(MainActivity.this)) {
                Toast.makeText(MainActivity.this, R.string.need_disable_accessibility_service, Toast.LENGTH_SHORT).show();
            } else if (BaseMethod.isNotificationListenerServiceEnabled(MainActivity.this)) {
                Toast.makeText(MainActivity.this, R.string.need_disable_notification_service, Toast.LENGTH_SHORT).show();
            } else if (!BaseMethod.isMIUI()) {
                Toast.makeText(MainActivity.this, R.string.only_support_miui_system, Toast.LENGTH_SHORT).show();
            } else {
                if (sharedPreferences.getInt(Config.PREFERENCE_CHOSEN_WIFI_LISTENER, -1) == Config.WIFI_LISTENER_TYPE.LOCAL_SERVICE.ordinal()) {
                    ListenerMethod.stopLocalListenerService(MainActivity.this);
                }
                Toast.makeText(MainActivity.this, R.string.enabled, Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, R.string.miui_system_support_attention, Toast.LENGTH_SHORT).show();
                sharedPreferences.edit().putInt(Config.PREFERENCE_CHOSEN_WIFI_LISTENER, chosenListenerType).apply();
            }
        } else if (chosenListenerType == Config.WIFI_LISTENER_TYPE.LOCAL_SERVICE.ordinal()) {
            if (BaseMethod.isAccessibilityServiceEnabled(MainActivity.this)) {
                Toast.makeText(MainActivity.this, R.string.need_disable_accessibility_service, Toast.LENGTH_SHORT).show();
            } else if (BaseMethod.isNotificationListenerServiceEnabled(MainActivity.this)) {
                Toast.makeText(MainActivity.this, R.string.need_disable_notification_service, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, R.string.enabled, Toast.LENGTH_SHORT).show();
                ListenerMethod.startLocalListenerService(MainActivity.this);
                sharedPreferences.edit().putInt(Config.PREFERENCE_CHOSEN_WIFI_LISTENER, chosenListenerType).apply();
            }
        }
    }

    private void setPermissionStatus() {
        Button grant = findViewById(R.id.button_grant_permission);
        if (PermissionMethod.hasPermission(this)) {
            grant.setText(R.string.granted);
            grant.setEnabled(false);
            findViewById(R.id.textView_permission_attention).setVisibility(View.GONE);
        } else {
            grant.setText(R.string.need_grant);
            grant.setEnabled(true);
            findViewById(R.id.textView_permission_attention).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionMethod.LOCATION_REQUEST_CODE) {
            boolean hasDenied = false;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    hasDenied = true;
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            if (!hasDenied) {
                Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
            setPermissionStatus();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
