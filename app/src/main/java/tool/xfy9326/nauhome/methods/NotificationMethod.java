package tool.xfy9326.nauhome.methods;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import tool.xfy9326.nauhome.R;
import tool.xfy9326.nauhome.activities.MainActivity;

public class NotificationMethod {
    public static final int NOTIFICATION_CODE_FOREGROUND_LISTENER = 1090;
    private static final int NOTIFICATION_CODE_LOGIN_NOTIFY = 1088;
    private static final String NOTIFY_CHANNEL_ID = "channel_notify";
    private static final String FOREGROUND_CHANNEL_ID = "channel_foreground";

    public static void reportLoginResult(@NonNull Context context, @NonNull String title, @NonNull String text) {
        showNotification(context, title, text);
    }

    private static void showNotification(@NonNull Context context, @NonNull String title, @NonNull String text) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFY_CHANNEL_ID);

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        builder.setContentText(text);
        builder.setAutoCancel(true);
        builder.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), PendingIntent.FLAG_UPDATE_CURRENT));
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setCategory(NotificationCompat.CATEGORY_STATUS);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        notificationManager.notify(NotificationMethod.NOTIFICATION_CODE_LOGIN_NOTIFY, builder.build());
    }

    public static Notification getForegroundNotification(@NonNull Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, FOREGROUND_CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        builder.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), PendingIntent.FLAG_UPDATE_CURRENT));
        builder.setContentTitle(context.getString(R.string.auto_login_service));
        return builder.build();
    }

    public static void createNotifyNotificationChannel(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(NOTIFY_CHANNEL_ID);
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(NOTIFY_CHANNEL_ID, context.getString(R.string.notification_channel), NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setDescription(context.getString(R.string.notification_channel_des));
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                notificationChannel.setShowBadge(true);
                notificationChannel.enableLights(false);
                notificationChannel.enableVibration(false);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    public static void createForegroundNotificationChannel(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(FOREGROUND_CHANNEL_ID);
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(FOREGROUND_CHANNEL_ID, context.getString(R.string.foreground_channel), NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setDescription(context.getString(R.string.foreground_channel_des));
                notificationChannel.setShowBadge(false);
                notificationChannel.enableLights(false);
                notificationChannel.enableVibration(false);
                notificationChannel.setSound(null, null);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }
}
