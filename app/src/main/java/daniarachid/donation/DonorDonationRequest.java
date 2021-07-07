package daniarachid.donation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
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

import java.util.HashMap;
import java.util.Map;

public class DonorDonationRequest extends AppCompatActivity {
    FirebaseFirestore fStore;
    ImageView imgItem;
    TextView mTitle, mQuantity, mDate, mStatus;
    Button btnApprove, btnReject;
    FloatingActionButton messageDonor;
    String requestId, title, itemId, requestDate, donorId, receiverId, quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_donation_request);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Initialize view
        fStore = FirebaseFirestore.getInstance();

        imgItem = findViewById(R.id.imgItem);
        mTitle = findViewById(R.id.txtTitle);
        mQuantity = findViewById(R.id.txtQuantity);
        mDate = findViewById(R.id.txtRequestDate);
        mStatus = findViewById(R.id.txtStatus);
        btnApprove = findViewById(R.id.btnApprove);
        btnReject = findViewById(R.id.btnReject);
        messageDonor = findViewById(R.id.msgReceiver);

        // retrieve data from intent
        displayRequestDetails();

        btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update status of request
                DocumentReference df = fStore.collection("DonationRequest").document(requestId);
                Map<String, Object> editedRequest = new HashMap<>();
                editedRequest.put("donorId", donorId);
                editedRequest.put("itemId", itemId);
                editedRequest.put("quantity", quantity);
                editedRequest.put("receiverId", receiverId);
                editedRequest.put("requestDate", requestDate);
                editedRequest.put("requestStatus", "Approved");
                df.update(editedRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Request has been approved", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                finish();
                startActivity(new Intent(getApplicationContext(), DonorRequestsList.class));

                //notify user
            }
        });

        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update status of request
                DocumentReference df = fStore.collection("DonationRequest").document(requestId);
                Map<String, Object> editedRequest = new HashMap<>();
                editedRequest.put("donorId", donorId);
                editedRequest.put("itemId", itemId);
                editedRequest.put("quantity", quantity);
                editedRequest.put("receiverId", receiverId);
                editedRequest.put("requestDate", requestDate);
                editedRequest.put("requestStatus", "Rejected");
                df.update(editedRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Request has been rejected", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                finish();
                startActivity(new Intent(getApplicationContext(), DonorRequestsList.class));
                //notify user

            }
        });
    }

    private void displayRequestDetails() {
        Intent intent = getIntent();
        requestId = intent.getStringExtra("requestId");
        title = intent.getStringExtra("title");



        //show details
        DocumentReference df = fStore.collection("DonationRequest").document(requestId);
        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                itemId= doc.get("itemId").toString();
                quantity = doc.get("quantity").toString();
                donorId = doc.get("donorId").toString();
                receiverId = doc.get("receiverId").toString();
                requestDate = doc.get("requestDate").toString();
                mQuantity.setText(doc.get("quantity").toString());
                mDate.setText(doc.get("requestDate").toString());
                mStatus.setText(doc.get("requestStatus").toString());
                mTitle.setText(title);
                //upload the picture
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference profileRef = storageReference.child("Items/" + itemId + "-" + title + ".jpg");
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signout: signout();
                break;
            case R.id.search: //search
                break;
            case R.id.userProfile:
                startActivity(new Intent(getApplicationContext(), UserProfile.class));
                break;
            case R.id.donationRequestsRec:
                startActivity(new Intent(getApplicationContext(), ReceiverRequestsList.class));
                break;
            case R.id.receivedDonationRequests:
                startActivity(new Intent(getApplicationContext(), DonorDonationRequest.class));
                break;
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}