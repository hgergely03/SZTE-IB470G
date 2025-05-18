package com.book.bookcorner;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class NotificationHandler {
    private NotificationManager notificationManager;
    private Context context;
    private static final String CHANNEL_ID = "bookcorner_notification_channel";
    private static final int NOTIFICATION_ID = 0;

    public NotificationHandler(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        createChannel();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Book Corner Notifications",
                NotificationManager.IMPORTANCE_HIGH
        );

        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(Color.GREEN);
        channel.setDescription("Notifications from your favourite book store");
        notificationManager.createNotificationChannel(channel);
    }

    public void sendNotification(String message) {
        Log.d("ORDERS_ACTIVITY", "sendNotification: " + message);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Book Corner")
                .setContentText(message)
                .setChannelId(CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.baseline_shopping_cart_checkout_24)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        this.notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
