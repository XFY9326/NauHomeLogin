package tool.xfy9326.nauhome.methods;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;

import androidx.annotation.NonNull;

import tool.xfy9326.nauhome.services.LocalListenerService;

public class ListenerMethod {

    public static ConnectivityManager.NetworkCallback startWifiListener(final Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkRequest.Builder builder = new NetworkRequest.Builder();

            NetworkRequest request = builder.addCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL)
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .build();
            ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
                private int netHashCode = 0;

                @Override
                public void onAvailable(@NonNull Network network) {
                    if (netHashCode == network.hashCode()) {
                        netHashCode = 0;
                    }
                    super.onAvailable(network);
                }

                @Override
                public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                    if (netHashCode != network.hashCode()) {
                        netHashCode = network.hashCode();
                        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL) && networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                            LoginInstance.getInstance().login(LoginInstance.REPORT_NOTIFICATION);
                        }
                    }
                    super.onCapabilitiesChanged(network, networkCapabilities);
                }

                @Override
                public void onLost(@NonNull Network network) {
                    if (netHashCode == network.hashCode()) {
                        netHashCode = 0;
                    }
                    super.onLost(network);
                }
            };
            connectivityManager.registerNetworkCallback(request, networkCallback);
            return networkCallback;
        }
        return null;
    }

    public static void startLocalListenerService(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, LocalListenerService.class));
        } else {
            context.startService(new Intent(context, LocalListenerService.class));
        }
    }

    public static void stopLocalListenerService(Context context) {
        context.stopService(new Intent(context, LocalListenerService.class));
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
