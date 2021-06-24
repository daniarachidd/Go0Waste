package daniarachid.donation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.core.content.ContextCompat.startActivity;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    List<String> titles, images, descriptions, quantities, productId, userIds, categories;
    LayoutInflater inflater;

    Adapter(Context ctx, List<String> titles, List<String> images, List<String> descriptions, List<String> quantities, List<String> categories, List<String> productId, List<String> userId) {

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
        View view = inflater.inflate(R.layout.custom_my_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.title.setText(titles.get(position));
        holder.description.setText(descriptions.get(position));
        holder.quantity.setText("Quantity " + quantities.get(position));


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
        TextView title, description, quantity;
        ImageView image;


        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtTitle);
            description = itemView.findViewById(R.id.txtDesc);
            quantity = itemView.findViewById(R.id.txtQuan);
            image = itemView.findViewById(R.id.donationItemImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(v.getContext(), "Item is clicked , " , Toast.LENGTH_SHORT).show();
                    FirebaseAuth fAuth = FirebaseAuth.getInstance();
                    String currentUserId = fAuth.getCurrentUser().getUid();

                    //how to get the current selected item?
                    String uId = userIds.get(getLayoutPosition());
                    Log.d("CompareID", "Current user Id " + currentUserId +"\n" +
                            "Item user Id " + uId);


                    // if the current userid = uid --> my items
                    if(uId.equals(currentUserId)) {
                        //Toast.makeText(v.getContext(), "user id " + uId, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(v.getContext(), MyItemView.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //i.putExtra("itemId", productId.get(getLayoutPosition()));
                        //i.putExtra("itemTitle", titles.get(getLayoutPosition()));
                        //i.putExtra("itemDesc", descriptions.get(getLayoutPosition()));
                        //i.putExtra("itemQuantity", quantities.get(getLayoutPosition()));
                        Map<String, Object> item = new HashMap<>();
                        item.put("itemId", productId.get(getLayoutPosition()));
                        item.put("title", titles.get(getLayoutPosition()));
                        item.put("description", descriptions.get(getLayoutPosition()));
                        item.put("quantity", quantities.get(getLayoutPosition()));
                        item.put("category", categories.get(getLayoutPosition()));

                        i.putExtra("item", (Serializable) item);

                        //i.putExtra("item")
                        v.getContext().startActivity(i);
                    } else {

                        //v.getContext().startActivity(new Intent(v.getContext(), Dona));
                    }

                }
            });
        }
    }
}
