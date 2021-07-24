package daniarachid.donation.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


import daniarachid.donation.R;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {
    List<String> itemIds;
    List<String> list;
    List<Integer> sorted;
    FirebaseFirestore fStore;
    LayoutInflater inflater;
    int count =0, reportType;
    int numberOfItems = 0;
    List<String> categories, donatedNo;
    public static final int DONATED_ITEMS_BY_CATEGORY = 1;
    public static final int DONATED_ITEMS_PER_DONOR = 2;
    public static final int DONOATED_ITEMS_PER_RECEIVER = 3;
    //int donatedItems[];






    public ReportAdapter(Context ctx, List<String> itemIds, List<String> categories, int reportType) {
        this.itemIds = itemIds;
        this.categories = categories;
        this.reportType = reportType;
        this.inflater = LayoutInflater.from(ctx);




    }

    public ReportAdapter(Context ctx, List<String> itemIds, List<String> categories, List<Integer> sorted, int reportType) {

        this.itemIds = itemIds;
        this.categories = categories;
        this.sorted = sorted;
        this.reportType = reportType;
        this.inflater = LayoutInflater.from(ctx);
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.custom_table_row, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        String pos = String.valueOf(position+1);
        if (categories != null && categories.size() > 0) {
            holder.rowNo.setText(pos);
        }
        switch (reportType) {
            case 1 :
                setReportByCategory(holder);
                break;
            case 2:
                setReportByDonor(holder);
                break;
            case 3:
                setReportByReceiver(holder);
                break;
            case 4:
                setReportByActiveCategoryAsc(holder);
                break;
        }



    }

    private void setReportByActiveCategoryAsc(ViewHolder holder) {
        holder.category.setText(categories.get(holder.getLayoutPosition()));
        holder.donatedNo.setText(sorted.get(holder.getLayoutPosition()).toString());
    }


    private void setReportByCategoryAsc(ViewHolder holder) {

        holder.category.setText(categories.get(holder.getLayoutPosition()));
        holder.donatedNo.setText(sorted.get(holder.getLayoutPosition()).toString());

    }


    private void setReportByReceiver(ViewHolder holder) {
        fStore = FirebaseFirestore.getInstance();
        //get the number of items
        fStore.collection("DonationRequest").whereEqualTo("requestStatus", "Donated")
                .whereEqualTo("receiverId", categories.get(holder.getLayoutPosition()))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        if (doc.exists()) {
                            numberOfItems++;
                        }

                    }

                }
                holder.donatedNo.setText(String.valueOf(numberOfItems));
                numberOfItems = 0;
            }
        });

        //get the receiver name
        fStore.collection("Users").document(categories.get(holder.getLayoutPosition())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        holder.category.setText(doc.get("name").toString());
                    }
                }
            }
        });
    }


    private void setReportByDonor(ViewHolder holder) {
        fStore = FirebaseFirestore.getInstance();

        //get the number of items
        fStore.collection("DonationRequest").whereEqualTo("requestStatus", "Donated")
                .whereEqualTo("donorId", categories.get(holder.getLayoutPosition()))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        if (doc.exists()) {
                            numberOfItems++;
                        }

                    }

                }
                holder.donatedNo.setText(String.valueOf(numberOfItems));
                numberOfItems = 0;
            }
        });
            //get the donor name
            fStore.collection("Users").document(categories.get(holder.getLayoutPosition())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            holder.category.setText(doc.get("name").toString());
                        }
                    }
                }
            });



    }

    private void setReportByCategory(ViewHolder holder) {
        /*
        list = new ArrayList<>();
        fStore = FirebaseFirestore.getInstance();
        holder.category.setText(categories.get(holder.getLayoutPosition()));
        switch (categories.get(holder.getLayoutPosition())) {
            case "Food":
                fStore.collection("Items").whereEqualTo("category", "Food").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        for (QueryDocumentSnapshot doc : task.getResult()) {
                                            for (int i = 0; i < itemIds.size(); i++) {
                                                if (doc.getId().equals(itemIds.get(i))) {
                                                    count++;


                                                }
                                            }

                                        }


                                        String counted = String.valueOf(count);
                                        holder.donatedNo.setText(counted);


                                        count = 0;
                                    }


                                }
                            }
                        });
                break;
            case "Women Clothes":
                fStore.collection("Items").whereEqualTo("category", "Women Clothes").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        for (QueryDocumentSnapshot doc : task.getResult()) {
                                            for (int i = 0; i < itemIds.size(); i++) {
                                                if (doc.getId().equals(itemIds.get(i))) {
                                                    count++;

                                                }
                                            }

                                        }
                                        String counted = String.valueOf(count);
                                        holder.donatedNo.setText(counted);


                                        count = 0;
                                    }


                                }
                            }
                        });
                break;
            case "Men Clothes" :
                fStore.collection("Items").whereEqualTo("category","Men Clothes").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        for (QueryDocumentSnapshot doc : task.getResult()) {
                                            for (int i = 0 ; i < itemIds.size(); i++) {
                                                if (doc.getId().equals(itemIds.get(i))) {
                                                    count ++;

                                                }
                                            }

                                        }

                                    } else {
                                        count = 0;

                                    }
                                    String counted = String.valueOf(count);
                                    holder.donatedNo.setText(counted);

                                    count = 0;


                                }
                            }
                        });
                break;
            case "Kids Clothes" :
                fStore.collection("Items").whereEqualTo("category","Kids Clothes").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        for (QueryDocumentSnapshot doc : task.getResult()) {
                                            for (int i = 0 ; i < itemIds.size(); i++) {
                                                if (doc.getId().equals(itemIds.get(i))) {
                                                    count ++;

                                                }
                                            }

                                        }
                                        String counted = String.valueOf(count);
                                        holder.donatedNo.setText(counted);

                                        count = 0;
                                    }


                                }
                            }
                        });
                break;
            case "Toys" :
                fStore.collection("Items").whereEqualTo("category","Toys").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        for (QueryDocumentSnapshot doc : task.getResult()) {
                                            for (int i = 0 ; i < itemIds.size(); i++) {
                                                if (doc.getId().equals(itemIds.get(i))) {
                                                    count ++;

                                                }
                                            }

                                        }

                                    } else {
                                        count = 0;
                                    }
                                    String counted = String.valueOf(count);
                                    holder.donatedNo.setText(counted);

                                    count = 0;


                                }
                            }
                        });
                break;
            case "Appliances" :
                fStore.collection("Items").whereEqualTo("category","Appliances").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        for (QueryDocumentSnapshot doc : task.getResult()) {
                                            for (int i = 0 ; i < itemIds.size(); i++) {
                                                if (doc.getId().equals(itemIds.get(i))) {
                                                    count ++;

                                                }
                                            }

                                        }
                                        String counted = String.valueOf(count);
                                        holder.donatedNo.setText(counted);

                                        count = 0;
                                    }


                                }
                            }
                        });
                break;
            default:
                holder.donatedNo.setText("Not found");

        }

         */
        holder.category.setText(categories.get(holder.getLayoutPosition()));
        holder.donatedNo.setText(sorted.get(holder.getLayoutPosition()).toString());
    }




    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView rowNo, category, donatedNo;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            rowNo = itemView.findViewById(R.id.rowNo);
            category = itemView.findViewById(R.id.category);
            donatedNo = itemView.findViewById(R.id.donatedNo);
        }
    }
}
