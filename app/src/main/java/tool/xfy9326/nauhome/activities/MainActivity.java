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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import tool.xfy9326.nauhome.Config;
import tool.xfy9326.nauhome.R;
import tool.xfy9326.nauhome.methods.BaseMethod;
import tool.xfy9326.nauhome.methods.PermissionMethod;
import tool.xfy9326.nauhome.services.LoginService;

public class MainActivity extends Activity {
    private SharedPreferences sharedPreferences;
    private int chosenListenerType = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.about_application);
        builder.setPositiveButton(android.R.string.yes, null);
        builder.show();
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
                if (PermissionMethod.hasPermission(MainActivity.this)) {
                    chosenListenerType = sharedPreferences.getInt(Config.PREFERENCE_CHOSEN_WIFI_LISTENER, 0);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(R.string.enable_listener);
                    builder.setSingleChoiceItems(listener, chosenListenerType, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            chosenListenerType = which;
                        }
                    });
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (chosenListenerType == Config.WIFI_LISTENER_TYPE.ACCESSIBILITY_SERVICE.ordinal()) {
                                if (BaseMethod.isNotificationListenerServiceEnabled(MainActivity.this)) {
                                    Toast.makeText(MainActivity.this, R.string.need_disable_notification_service, Toast.LENGTH_SHORT).show();
                                } else {
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
                                    Toast.makeText(MainActivity.this, R.string.enabled, Toast.LENGTH_SHORT).show();
                                    sharedPreferences.edit().putInt(Config.PREFERENCE_CHOSEN_WIFI_LISTENER, chosenListenerType).apply();
                                }
                            }
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, null);
                    builder.show();
                } else {
                    Toast.makeText(MainActivity.this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginID.setText(sharedPreferences.getString(Config.PREFERENCE_LOGIN_ID, ""));
        password.setText(sharedPreferences.getString(Config.PREFERENCE_PASSWORD, ""));
        String savedType = sharedPreferences.getString(Config.PREFERENCE_LOGIN_TYPE, "");
        for (int i = 0; i < login_type.length; i++) {
            if (login_type[i].equals(savedType)) {
                loginType.setSelection(i);
                break;
            }
        }

        final Button save = findViewById(R.id.button_save);
        Button grant = findViewById(R.id.button_grant_permission);
        Button login = findViewById(R.id.button_login);
        Button logout = findViewById(R.id.button_logout);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionMethod.hasPermission(MainActivity.this)) {
                    startService(new Intent(MainActivity.this, LoginService.class)
                            .putExtra(LoginService.OPERATION_TAG, LoginService.OPERATION_LOGIN)
                            .putExtra(LoginService.REPORT_TAG, LoginService.REPORT_TOAST));
                } else {
                    Toast.makeText(MainActivity.this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionMethod.hasPermission(MainActivity.this)) {
                    startService(new Intent(MainActivity.this, LoginService.class)
                            .putExtra(LoginService.OPERATION_TAG, LoginService.OPERATION_LOGOUT)
                            .putExtra(LoginService.REPORT_TAG, LoginService.REPORT_TOAST));
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
