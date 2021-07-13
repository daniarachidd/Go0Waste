package daniarachid.donation.Messaging;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import daniarachid.donation.Entity.MessageModel;

public class MessageRepository {

    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth ;
    String userId;
    List<MessageModel> messageModelsList = new ArrayList<>();
    OnMessageAdded onMessageAdded;

    public MessageRepository(OnMessageAdded onMessageAdded) {
        this.onMessageAdded = onMessageAdded;
    }

    public void getAllMessages(String receiverId) {
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();



        //snapShotListener to see the messages as added
        fStore.collection("Messages").orderBy("time").
        addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                messageModelsList.clear();
                try {
                    for (DocumentSnapshot doc : value.getDocuments()) {

                        MessageModel messageModel = doc.toObject(MessageModel.class);


                        //get the conversation between the 2 users
                        if (messageModel.getSender().equals(userId) && messageModel.getReceiver().equals(receiverId) ||
                                messageModel.getReceiver().equals(userId) && messageModel.getSender().equals(receiverId)) {

                            messageModelsList.add(messageModel);

                            //Collections.reverse(messageModelsList);
                            //messageModelsList.add(messageModelsList.size(), messageModel);
                            onMessageAdded.messagesFromFirestore(messageModelsList);
                        }




                    }
                }
                catch (NullPointerException e) {
                    Log.d("TAG", "No messages");
                }

            }
        });


    }


    public void deleteAllMessages(String receiverId) {
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        fStore.collection("Messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                try {
                    for (DocumentSnapshot doc : value.getDocuments()) {

                        MessageModel messageModel = doc.toObject(MessageModel.class);
                        //get the conversation between the 2 users
                        if (messageModel.getSender().equals(userId) && messageModel.getReceiver().equals(receiverId) ||
                                messageModel.getReceiver().equals(userId) && messageModel.getSender().equals(receiverId)) {
                            //delete
                            fStore.collection("Messages").document(doc.getId()).delete();
                        }


                    }
                } catch (NullPointerException e) {
                    Log.d(" TAG", "No messages");
                }

            }
        });

    }


    public  interface  OnMessageAdded{
        void messagesFromFirestore(List<MessageModel> messageModels);
    }
}
