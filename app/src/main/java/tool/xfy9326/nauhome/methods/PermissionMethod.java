package tool.xfy9326.nauhome.methods;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

public class PermissionMethod {
    public static final int LOCATION_REQUEST_CODE = 1;

    public static boolean hasPermission(Context context) {
        return context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean requestLocationPermission(Activity activity) {
        if (!hasPermission(activity)) {
            activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return false;
        }
        return true;
    }
}
