package daniarachid.donation.Messaging;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
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
        fStore.collection("Messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                messageModelsList.clear();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    MessageModel messageModel = doc.toObject(MessageModel.class);


                    Log.d("CheckMe", "Sender : " + messageModel.getSender());
                    Log.d("CheckMe", "Receiver : " +  messageModel.getReceiver());

                    //get the conversation between the 2 users
                    if (messageModel.getSender().equals(userId) && messageModel.getReceiver().equals(receiverId) ||
                            messageModel.getReceiver().equals(userId) && messageModel.getSender().equals(receiverId)) {

                         messageModelsList.add(messageModel);
                         onMessageAdded.messagesFromFirestore(messageModelsList);
                    }

                }
            }
        });


    }


    public  interface  OnMessageAdded{
        void messagesFromFirestore(List<MessageModel> messageModels);
    }
}
