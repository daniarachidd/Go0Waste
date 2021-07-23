package daniarachid.donation.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import daniarachid.donation.Messaging.Conversation;
import daniarachid.donation.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    List<String> userId, messages, dates, receiverId;
    LayoutInflater inflater;
    String userName;

    public ChatAdapter(Context ctx, List<String> userId, List<String> message, List<String> date, List<String> receiverId) {
        this.userId = userId;

        this.messages = message;
        this.dates = date;
        this.receiverId = receiverId;


        this.inflater= LayoutInflater.from(ctx);
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_single_chat_item, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {


            holder.message.setText(messages.get(position));
            holder.date.setText(dates.get(position).substring(0,10));

            //get the pic
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference profileRef = storageReference.child("Users/" + userId.get(position) + "profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(holder.userImage);
                }
            });

            //get the user name

            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
            DocumentReference doc = fStore.collection("Users").document(receiverId.get(position));
            doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            userName = document.getString("name");
                            holder.userName.setText(userName);
                        }
                    }
                }
            });




    }

    @Override
    public int getItemCount() {
        return userId.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userImage;
        TextView userName, message, date, empty;
        public ViewHolder(@NonNull @NotNull View itemView) {

            super(itemView);
            userImage = itemView.findViewById(R.id.chatUserImage);
            userName = itemView.findViewById(R.id.txtUserName);
            message = itemView.findViewById(R.id.txtContent);
            date = itemView.findViewById(R.id.txtDate);

            //empty = itemView.findViewById(R.id.txtEmptyChat);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth fAuth = FirebaseAuth.getInstance();
                    String currentUser = fAuth.getCurrentUser().getUid();
                    String receiver = receiverId.get(getLayoutPosition());


                    //Log.d("CheckMe", receiverId);
                    Intent intent = new Intent(v.getContext(), Conversation.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("receiverId", receiver);
                    intent.putExtra("senderId", currentUser);
                    v.getContext().startActivity(intent);
                        }
                    });
                    //open the chat



        }
    }


}
