package daniarachid.donation.Notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.lang.annotation.Target;

class   OreoNotification extends ContextWrapper {
    public static final String CHANNEL_ID = "daniarachid.donation";
    public static final String  NAME = "Chat";
    NotificationManager notificationManager;

    public OreoNotification(Context base) {
        super(base);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            createChannel();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(
                CHANNEL_ID, NAME, notificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(notificationChannel);


    }

    public NotificationManager getManager() {

        if(notificationManager == null) {

            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        }

        return notificationManager;
    }







    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getNotification(String title, String body, PendingIntent pIntent, Uri soundUri, String icon) {

        return  new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(soundUri)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setSmallIcon(Integer.parseInt(icon));




    }
}


