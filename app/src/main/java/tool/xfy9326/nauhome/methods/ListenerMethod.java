package tool.xfy9326.nauhome.methods;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.util.Log;

import androidx.annotation.NonNull;

import tool.xfy9326.nauhome.services.LoginService;

public class ListenerMethod {
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

    public static ConnectivityManager.NetworkCallback startWifiListener(final Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkRequest.Builder builder = new NetworkRequest.Builder();

            NetworkRequest request = builder.addCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL)
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .build();
            ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
                private boolean connectToNewNetwork = false;
                private boolean linkPropertiesChanged = false;

                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    connectToNewNetwork = true;
                }

                @Override
                public void onLinkPropertiesChanged(@NonNull Network network, @NonNull LinkProperties linkProperties) {
                    super.onLinkPropertiesChanged(network, linkProperties);
                    linkPropertiesChanged = true;
                }

                @Override
                public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                    if (connectToNewNetwork && linkPropertiesChanged) {
                        connectToNewNetwork = false;
                        linkPropertiesChanged = false;
                        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL) &&
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                            Log.d("AutoLogin", "NetworkCallback Start Login Service");
                            startLoginService(context);
                        }
                    }
                    super.onCapabilitiesChanged(network, networkCapabilities);
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    connectToNewNetwork = false;
                    linkPropertiesChanged = false;
                }
            };
            connectivityManager.registerNetworkCallback(request, networkCallback);
            return networkCallback;
        }
        return null;
    }

    public static void startLoginService(Context context) {
        context.startService(new Intent(context, LoginService.class)
                .putExtra(LoginService.OPERATION_TAG, LoginService.OPERATION_LOGIN)
                .putExtra(LoginService.REPORT_TAG, LoginService.REPORT_NOTIFICATION));
    }

    public static ConnectivityManager.NetworkCallback stopWifiListener(Context context, ConnectivityManager.NetworkCallback networkCallback) {
        if (networkCallback != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                connectivityManager.unregisterNetworkCallback(networkCallback);
                return null;
            }
        }
        return networkCallback;
    }
}
