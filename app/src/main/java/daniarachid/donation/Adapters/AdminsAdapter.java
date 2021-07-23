package daniarachid.donation.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import daniarachid.donation.Administration.AdminControl;
import daniarachid.donation.DonationManagement.MyItemView;
import daniarachid.donation.DonationManagement.TestMyItem;
import daniarachid.donation.R;

public class AdminsAdapter extends RecyclerView.Adapter<AdminsAdapter.ViewHolder> {

    List<String> adminId, email, name;
    LayoutInflater inflater;
    Context ctx;

    public AdminsAdapter(Context ctx, List<String> adminId, List<String> name, List<String> email) {
        this.adminId = adminId;
        this.email = email;
        this.name = name;
        this.ctx = ctx;
        inflater = LayoutInflater.from(ctx);
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_admin_view, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.adminName.setText(name.get(holder.getLayoutPosition()));
        //final Context ctx = holder.btnRemove.getContext();
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                //View dialogView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.dial)

                builder.setTitle("Remove Admin");
                builder.setMessage("Are you sure you want to remove this admin? ");
                builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete
                        fStore.collection("Users").document(adminId.get(holder.getLayoutPosition()))
                                .update("isAuthorized", false );

                        Toast.makeText(v.getRootView().getContext(), "Admin is removed", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        //notifyItemRemoved(holder.getLayoutPosition());
                        //AdminsAdapter adapter = AdminsAdapter.this;

                        Intent intent = new Intent(v.getRootView().getContext(), AdminControl.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        ctx.startActivity(intent);


                        //adapter.notifyItemRemoved(holder.getLayoutPosition());



                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });


    }

    @Override
    public int getItemCount() {
        return adminId.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView adminName;
        Button btnRemove;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            adminName = itemView.findViewById(R.id.txtAdminName);
            btnRemove = itemView.findViewById(R.id.btnRemove);





        }


    }
}
