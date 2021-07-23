package daniarachid.donation.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import daniarachid.donation.R;

public class HistoryRequestAdapter  extends RecyclerView.Adapter<HistoryRequestAdapter.ViewHolder> {
    List<String> requestId, itemId, dates, userId;
    LayoutInflater inflater;

    //try something like checking if the userid == donorId | receiverId and initialize the views accordingly


    public HistoryRequestAdapter(Context ctx, List<String> requestId, List<String> itemId, List<String> dates, List<String> userId) {
        this.requestId = requestId;
        this.itemId = itemId;
        this.userId = userId;

        this.dates = dates;
        inflater = LayoutInflater.from(ctx);
    }


    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_donor_donation_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        //set the values of views here
        //get the title
        FirebaseFirestore fStore= FirebaseFirestore.getInstance();

        DocumentReference document = fStore.collection("Items").document(itemId.get(position));
        document.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                if(doc.exists()) {
                    holder.title.setText(doc.get("title").toString());


                    //set the picture
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                    StorageReference profileRef = storageReference.child("Items/" + itemId.get(holder.getLayoutPosition()) + "-" + doc.get("title") + ".jpg");
                    profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(holder.itemImg);
                        }

                    });
                }
            }
        });

        //get the username
        document = fStore.collection("Users").document(userId.get(position));
        document.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()) {
                    holder.receiverName.setText(doc.get("name").toString());
                }
            }
        });

        //set the values
        holder.date.setText(dates.get(position));
        holder.receiverTitle.setText("Receiver");

        //get the image



    }

    @Override
    public int getItemCount() {
        return requestId.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView title, receiverTitle, receiverName, dateTitle, date;
        ImageView itemImg;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.itemName);
            receiverTitle = itemView.findViewById(R.id.txtStatusTitle);
            receiverName = itemView.findViewById(R.id.txtStatus);
            date = itemView.findViewById(R.id.txtDateInfo);
            dateTitle = itemView.findViewById(R.id.txtDateTitle);
            date.setVisibility(View.VISIBLE);
            dateTitle.setVisibility(View.VISIBLE);
            itemImg = itemView.findViewById(R.id.imgRequestItem);
        }
    }
}
