package daniarachid.donation.DonationRequestManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import daniarachid.donation.UserAccount.MainActivity;
import daniarachid.donation.R;
import daniarachid.donation.Messaging.Conversation;

public class ReceiverDonationRequestReview extends AppCompatActivity {
    FirebaseFirestore fStore;
    ImageView imgItem;
    TextView mTitle, mQuantity, mDate, mStatus;
    Button cancelRequest;
    FloatingActionButton btnSendMsg;
    String requestId, title, itemId, token, userId, donorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_donation_request_review);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Initialize view
        fStore = FirebaseFirestore.getInstance();

        imgItem = findViewById(R.id.imgItem);
        mTitle = findViewById(R.id.txtTitle);
        mQuantity = findViewById(R.id.txtQuantity);
        mDate = findViewById(R.id.txtRequestDate);
        mStatus = findViewById(R.id.txtStatus);
        cancelRequest = findViewById(R.id.btnCancelRequest);
        btnSendMsg = findViewById(R.id.btnSendMessage);

        // retrieve data from intent
        displayRequestDetails();

        cancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //confirm
                AlertDialog.Builder builder = new AlertDialog.Builder(ReceiverDonationRequestReview.this);
                builder.setTitle("Cancel Request?");
                builder.setMessage("This will cancel your donation request for the selected item");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete the request from firestore
                        DocumentReference docRef = fStore.collection("DonationRequest").document(requestId);
                        docRef.delete();
                        Toast.makeText(ReceiverDonationRequestReview.this, "Your request has been canceled", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), TestReceiverRequestList.class));
                        finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();



            }
        });

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open chat activity + pass (sender, receiver)

                Intent intent = new Intent(getApplicationContext(), Conversation.class);
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                userId = fAuth.getCurrentUser().getUid();
                intent.putExtra("senderId", userId);
                intent.putExtra("receiverId", donorId);
                //Log.d("CheckMe", donorId);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);


            }
        });




    }



    private void displayRequestDetails() {
        Intent intent = getIntent();
        requestId = intent.getStringExtra("requestId");
        title = intent.getStringExtra("title");
        donorId = intent.getStringExtra("donorId");



        //show details
        DocumentReference df = fStore.collection("DonationRequest").document(requestId);
        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                itemId= doc.get("itemId").toString();
                mQuantity.setText(doc.get("quantity").toString());
                mDate.setText(doc.get("requestDate").toString());
                mStatus.setText(doc.get("requestStatus").toString());
                mTitle.setText(title);
                //upload the picture
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference profileRef = storageReference.child("Items/" + itemId + ".jpg");
                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(imgItem);
                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void signout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();;

    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_delete, menu);
        return true;
    }

*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}