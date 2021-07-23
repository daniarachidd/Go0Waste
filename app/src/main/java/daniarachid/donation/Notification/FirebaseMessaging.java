package daniarachid.donation.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.RemoteInput;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.navigation.NavDeepLinkBuilder;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

import daniarachid.donation.DonationRequestManagement.DonorDonationRequest;
import daniarachid.donation.DonationRequestManagement.ReceiverDonationRequestReview;
import daniarachid.donation.Messaging.Conversation;
import daniarachid.donation.R;

public class FirebaseMessaging  extends FirebaseMessagingService {

    public static final int  MESSAGE_NOTIFICATION =1;
    public static final int DONATION_REQUEST_NOTIFICATION = 2;
    public static final int REQUEST_STATUS_NOTIFICATION = 3;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String sent = remoteMessage.getData().get("sent");
        String user = remoteMessage.getData().get("user");
        
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!= null) {
            assert sent != null;

            if(sent.equals(firebaseUser.getUid())) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    sendOreoNotification(remoteMessage);

                } else {
                    
                    sendNotification(remoteMessage);

                }
            }
        }
    }







    private void sendNotification(RemoteMessage remoteMessage) {
        Log.d("CheckMe", "Send normal Notification");

        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        int type = Integer.parseInt(remoteMessage.getData().get("type"));



        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
      Intent intent = new Intent();
        Bundle bundle = new Bundle();
        switch (type) {
            case MESSAGE_NOTIFICATION:
                 intent = new Intent(this, Conversation.class);
                break;
            case DONATION_REQUEST_NOTIFICATION:
                 intent = new Intent(this, DonorDonationRequest.class);
                 ;
                break;
            case REQUEST_STATUS_NOTIFICATION:
                 intent = new Intent(this, ReceiverDonationRequestReview.class);
        }

        //Intent intent = new Intent(this, Conversation.class);


        bundle.putString("receiverId", user);



        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);


        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);
        NotificationManager noti = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        int i = 0;
        if (j > 0){
            i = j;
        }

        noti.notify(j, builder.build());
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private void sendOreoNotification(RemoteMessage remoteMessage) {
        //Log.d("CheckMe", "send Oreo Notification");
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String requestId = remoteMessage.getData().get("requestId");
        String requestedItemTitle = remoteMessage.getData().get("requestedItemTitle");
        int type = Integer.parseInt(remoteMessage.getData().get("type"));


        //might need the intent so check later

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));

        Intent intent = new Intent();

        switch (type) {
            case MESSAGE_NOTIFICATION:
                //check this out
               // user = remoteMessage.getData().get("")

                intent = new Intent(this, Conversation.class);

                break;
            case DONATION_REQUEST_NOTIFICATION:
                intent = new Intent(this, DonorDonationRequest.class);
                intent.putExtra("requestId", requestId);
                intent.putExtra("title", requestedItemTitle);
                break;
            case REQUEST_STATUS_NOTIFICATION:
                intent = new Intent(this, ReceiverDonationRequestReview.class);
                intent.putExtra("requestId", requestId);
                intent.putExtra("title", requestedItemTitle);
                intent.putExtra("donorId", user);
        }

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String sender = fAuth.getCurrentUser().getUid();

        Bundle bundle = new Bundle();
        bundle.putString("receiverId", user);
        bundle.putString("senderId", sender);


        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);


        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification notification1 = new OreoNotification(this);
        Notification.Builder builder  = notification1.getNotification(title, body, pendingIntent, defaultSound, icon);




        int i = 0;
        if (j > 0){
            i = j;
        }

        notification1.getManager().notify(j, builder.build());




    }
}
