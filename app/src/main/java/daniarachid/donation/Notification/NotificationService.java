package daniarachid.donation.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static daniarachid.donation.Notification.OreoNotification.CHANNEL_ID;

public class NotificationService extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput!= null) {

            String replyText = remoteInput.getString("key_text_reply");
            FirebaseAuth fAuth = FirebaseAuth.getInstance();
            String userId = fAuth.getCurrentUser().getUid();

            SharedPreferences sharedPreferences = context.getSharedPreferences("PREFS",
                    (Context.MODE_PRIVATE));

            String friendId = sharedPreferences.getString("friendId", "");

            //get the time for the reply
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            String currentTime = formatter.format(date);


            //store the reply to firebase
            Map<String, Object> message = new HashMap<>();
            message.put("sender", userId);
            message.put("receiver", friendId);
            message.put("message", replyText);
            message.put("time", currentTime);

            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
            fStore.collection("Messages").document(currentTime).set(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                }
            });

            SharedPreferences pref = context.getSharedPreferences("NEWPREFS", Context.MODE_PRIVATE);
            int dummy = pref.getInt("values", 0);
            Notification repliedNotif =
                    new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentText("Reply Received").build();

            notificationManager.notify(dummy, repliedNotif);

        }

    }
}
