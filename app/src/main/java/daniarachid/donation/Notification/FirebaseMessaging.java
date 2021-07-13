package daniarachid.donation.Notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.RemoteInput;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.navigation.NavDeepLinkBuilder;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import daniarachid.donation.Messaging.Conversation;
import daniarachid.donation.R;

public class FirebaseMessaging  extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String sent = remoteMessage.getData().get("sent");
        String user = remoteMessage.getData().get("user");
        
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!= null) {

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

        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        //might need the intent so check later

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));

        Bundle bundle = new Bundle();
        bundle.putString("friendid", user);

        /*
        PendingIntent pendingStuff = new NavDeepLinkBuilder(
                getApplicationContext()).setGraph(R.navigation.nav_graph)
                .setArguments(bundle).setDestination(R.id.nav_host_fragment_content_test_chat)
                .createPendingIntent();

         */
        PendingIntent pendingStuff = new NavDeepLinkBuilder(getApplicationContext()).setArguments(bundle)
                .setDestination(R.id.chatActivity).createPendingIntent();


        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingStuff);
        NotificationManager noti = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        int i = 0;
        if (j > 0){
            i = j;
        }


        SharedPreferences shf = getSharedPreferences("NEWPRFS", MODE_PRIVATE);
        SharedPreferences.Editor editorShf = shf.edit();
        editorShf.putInt("values", i);
        editorShf.apply();
        noti.notify(i, builder.build());
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
    private void sendOreoNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Bundle bundle = new Bundle();
        bundle.putString("friendId", user);

        SharedPreferences sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        SharedPreferences.Editor predsfits = sharedPreferences.edit();
        predsfits.putString("friendId", user);
        predsfits.apply();



        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        
        OreoNotification oreoNotification = new OreoNotification(this);

        //for action reply
        RemoteInput remoteInput = new RemoteInput.Builder("key_text_reply").setLabel("Your Message...").build();
        Intent replyIntent;
        PendingIntent pIntentreply = null;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            replyIntent = new Intent(this, NotificationService.class);
            pIntentreply = PendingIntent.getBroadcast(this, 0 , replyIntent, 0);
            
        }

        // reply to a message in the notification
        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(R.drawable.reply,
                "Reply", pIntentreply).addRemoteInput(remoteInput).build();




        //check the destination 32:47
        //taking us to the conversation
        PendingIntent pendingStuff = new NavDeepLinkBuilder(
                getApplicationContext())
                .setArguments(bundle).setDestination(R.id.chatActivity)
                .createPendingIntent();

        NotificationCompat.Builder builder = oreoNotification.getNotificationStuff(replyAction,
                title, body, pendingStuff, defaultSound, icon);

        int i = 0;
        if (j > 0) {
            i = j;
        }

        //update the notification

        SharedPreferences shf = getSharedPreferences("NEWPRFS", MODE_PRIVATE);
        SharedPreferences.Editor editorShf = shf.edit();
        editorShf.putInt("values", i);
        editorShf.apply();

        oreoNotification.getManager().notify(i, builder.build());




    }
}
