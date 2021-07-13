package daniarachid.donation.Adapters;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import daniarachid.donation.Entity.MessageModel;
import daniarachid.donation.Messaging.Conversation;
import daniarachid.donation.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    List<MessageModel> messageModelList;
    public static final int MESSAGE_RIGHT = 0;
    public static final int MESSAGE_LEFT = 1;
    String senderId, receiverId;

    public MessageAdapter(String senderId, String receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public void setMessageModelList(List<MessageModel> messageModelList) {
        this.messageModelList= messageModelList;
    }


    @NonNull
    @NotNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (viewType == MESSAGE_RIGHT) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_right, parent, false);
            return new MessageHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_left, parent, false);
            return new MessageHolder(view);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MessageHolder holder, int position) {
        holder.showMessage.setVisibility(View.VISIBLE);
        holder.time.setVisibility(View.VISIBLE);
        holder.showMessage.setText(messageModelList.get(position).getMessage());
        holder.time.setText(messageModelList.get(position).getTime().substring(11,17));
        //holder.time.setText(messageModelList.get(position).getTime().toString());

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();

        if (getItemViewType(position) == MESSAGE_RIGHT) {
            //get the user pic
            //.
            StorageReference profileRef= storageReference.child("Users/" +senderId + "profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(holder.image);
                }
            });

        } else {
            StorageReference profileRef= storageReference.child("Users/" +receiverId + "profile.jpg");

            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(holder.image);
                }
            });

            //get the receiver pic
        }


    }

    @Override
    public int getItemCount() {
        if (messageModelList == null) {
            return 0;
        } else {
            return messageModelList.size();
        }

    }

    class MessageHolder extends  RecyclerView.ViewHolder {
        TextView showMessage, time;
        de.hdodenhof.circleimageview.CircleImageView image;

        public MessageHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            showMessage = itemView.findViewById(R.id.show_message);
            time = itemView.findViewById(R.id.displaytime);
            image = itemView.findViewById(R.id.chatUserImage);



        }

    }

    @Override
    public int getItemViewType(int position) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        MessageModel messageModel = messageModelList.get(position);
        if(messageModel.getSender().equals(fAuth.getCurrentUser().getUid())) {
            return MESSAGE_RIGHT;
        }
        else {
            return MESSAGE_LEFT;
        }

    }
}
