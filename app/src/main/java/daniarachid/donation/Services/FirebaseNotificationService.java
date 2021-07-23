package daniarachid.donation.Services;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class FirebaseNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }

    @Override
    public void onNewToken(String s) {
        updateToken(s);
        super.onNewToken(s);
    }

    private void updateToken(String token) {
        String userId;
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("Users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> map = new HashMap<>();
                map.put("token", token);
                fStore.collection("Tokens").document(userId).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("CheckMe", "Token is updated");
                    }
                });

            }
        });

    }
}
