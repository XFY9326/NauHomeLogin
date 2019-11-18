package tool.xfy9326.nauhome.methods;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import tool.xfy9326.nauhome.R;

public class NotificationMethod {
    private static final int NOTIFICATION_CODE_NAU_LOGIN = 815;
    private static final String CHANNEL_ID = "channel_nau_login";

    public static void reportLoginResult(Context context, String title, String text) {
        showNotification(context, title, text);
    }

    private static void showNotification(Context context, String title, String text) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);

            createNotificationChannel(context, notificationManager);

            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle(title);
            builder.setContentText(text);
            builder.setAutoCancel(true);
            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            builder.setCategory(NotificationCompat.CATEGORY_MESSAGE);
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                    R.mipmap.ic_launcher));
            notificationManager.notify(NotificationMethod.NOTIFICATION_CODE_NAU_LOGIN, builder.build());
        }
    }

    private static void createNotificationChannel(@NonNull Context context, @NonNull NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(CHANNEL_ID, context.getString(R.string.notification_channel), NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setDescription(context.getString(R.string.notification_channel_des));
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                notificationChannel.setShowBadge(true);
                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(true);
                notificationChannel.setShowBadge(false);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }
}
