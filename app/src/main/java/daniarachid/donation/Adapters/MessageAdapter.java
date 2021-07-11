package daniarachid.donation.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.List;

import daniarachid.donation.Entity.MessageModel;
import daniarachid.donation.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    List<MessageModel> messageModelList;
    public static final int MESSAGE_RIGHT = 0;
    public static final int MESSAGE_LEFT = 1;


    public void setMessageModelList(List<MessageModel> messageModelList) {
        this.messageModelList= messageModelList;
    }


    @NonNull
    @NotNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (viewType == MESSAGE_RIGHT) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_right, parent, false);
            return new MessageHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_left, parent, false);
            return new MessageHolder(view);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MessageHolder holder, int position) {
        holder.showMessage.setVisibility(View.VISIBLE);
        holder.time.setVisibility(View.VISIBLE);
        holder.showMessage.setText(messageModelList.get(position).getMessage());
        holder.time.setText(messageModelList.get(position).getTime().substring(0,5));


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

        public MessageHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            showMessage = itemView.findViewById(R.id.show_message);
            time = itemView.findViewById(R.id.displaytime);


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
