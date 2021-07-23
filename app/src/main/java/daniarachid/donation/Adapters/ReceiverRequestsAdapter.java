package daniarachid.donation.Adapters;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import daniarachid.donation.DonationRequestManagement.DonorDonationRequest;
import daniarachid.donation.R;
import daniarachid.donation.DonationRequestManagement.ReceiverDonationRequestReview;

public class ReceiverRequestsAdapter extends RecyclerView.Adapter<ReceiverRequestsAdapter.ViewHolder> {
    List<String> statusList, titlesList, donorIds, itemIds, requestId;
    LayoutInflater inflater;
    String userId;
    FirebaseAuth fAuth;


    public ReceiverRequestsAdapter(Context ctx, List<String> requestId, List<String> donorIds, List<String> itemIds, List<String> statusList) {

        this.requestId= requestId;
        this.donorIds = donorIds;
        this.itemIds = itemIds;
        this.statusList = statusList;
        this.inflater = LayoutInflater.from(ctx);
        titlesList = new ArrayList<>();
    }




    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_receiver_donation_request, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        //holder.title.setText(titles.get(position));
        //retrieve the titles



        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DocumentReference df = fStore.collection("Items").document(itemIds.get(position));
        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()) {
                    String title = doc.get("title").toString();
                    titlesList.add(title);
                    holder.title.setText(doc.get("title").toString());

                    //get the picture of the item
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                    StorageReference profileRef = storageReference.child("Items/" + itemIds.get(position) + "-" + title + ".jpg");
                    profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(holder.requestedItem);
                        }
                    });
                }




            }
        });
        //get the status of the request
        String status = statusList.get(position);
        holder.status.setText(status);

    }

    @Override
    public int getItemCount() {
        return itemIds.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {
        ImageView requestedItem;
        TextView title, status;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.itemName);
            status = itemView.findViewById(R.id.txtStatus);
            requestedItem = itemView.findViewById(R.id.imgRequestItem);

            //set on click listener here
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //if receiver request
                    fAuth = FirebaseAuth.getInstance();
                    userId = fAuth.getCurrentUser().getUid();
                    Intent intent;
                    if(userId.equals(donorIds.get(getLayoutPosition()))) {
                        //show donor dontion requests
                        intent = new Intent(v.getContext(), DonorDonationRequest.class);
                        intent.putExtra("requestId", requestId.get(getLayoutPosition()));
                        intent.putExtra("title", titlesList.get(getLayoutPosition()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        v.getContext().startActivity(intent);


                    } else {


                        //show receiver donation requests
                        intent = new Intent(v.getContext(), ReceiverDonationRequestReview.class);
                        intent.putExtra("requestId", requestId.get(getLayoutPosition()));
                        intent.putExtra("title", titlesList.get(getLayoutPosition()));
                        intent.putExtra("donorId", donorIds.get(getLayoutPosition()));
                        intent.putExtra("userId", userId);

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        v.getContext().startActivity(intent);
                    }




                }
            });
        }
    }
}
