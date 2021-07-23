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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daniarachid.donation.DonationManagement.DonationItemView;
import daniarachid.donation.R;

public class MainDonationViewAdapter extends RecyclerView.Adapter<MainDonationViewAdapter.ViewHolder> {

    List<String> titles, images, descriptions, quantities, productId, userIds, categories, donors;
    //String donor;
    LayoutInflater inflater;

    public MainDonationViewAdapter(Context ctx, List<String> titles, List<String> images, List<String> descriptions, List<String> quantities, List<String> categories, List<String> productId, List<String> userId) {

        this.descriptions = descriptions;
        this.titles = titles;
        this.images = images;
        this.quantities = quantities;
        this.categories = categories;
        this.productId = productId;
        this.userIds = userId;

        this.inflater = LayoutInflater.from(ctx);

    }



    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.custom_donation_items, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        //String className = getClass().getName();
       // Log.d("TAG", "the Class name is: " +className);
        holder.title.setText(titles.get(position));
       // holder.description.setText(descriptions.get(position));
      //  holder.quantity.setText("Quantity " + quantities.get(position));
        //holder.category.setText(categories.get(position));

        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DocumentReference dr = fStore.collection("Users").document(userIds.get(position));
        dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String donorName = documentSnapshot.get("name").toString();

                holder.donor.setText("Donor "+ donorName);
            }
        });



        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("Items/" + productId.get(position) + "-" + titles.get(position) + ".jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(holder.image);
            }
        });

    }

    @Override
    public int getItemCount() {
        return titles.size();
    }


    public  class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, quantity, donor, category;
        ImageView image;


        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtTitle);
            description = itemView.findViewById(R.id.txtDesc);
            quantity = itemView.findViewById(R.id.txtQuan);
            donor = itemView.findViewById(R.id.txtDonor);
            category = itemView.findViewById(R.id.txtRequestDate);
            image = itemView.findViewById(R.id.donationItemImage);

            //item clicked by the user
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        Intent i = new Intent(v.getContext(), DonationItemView.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        Map<String, Object> item = new HashMap<>();
                        item.put("itemId", productId.get(getLayoutPosition()));
                        item.put("title", titles.get(getLayoutPosition()));
                        item.put("description", descriptions.get(getLayoutPosition()));
                        item.put("quantity", quantities.get(getLayoutPosition()));
                        item.put("category", categories.get(getLayoutPosition()));
                        item.put("donorId", userIds.get(getLayoutPosition()));

                        i.putExtra("item", (Serializable) item);

                        //i.putExtra("item")
                        v.getContext().startActivity(i);


                }
            });
        }
    }
}
