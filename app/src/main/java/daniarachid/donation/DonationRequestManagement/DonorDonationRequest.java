package daniarachid.donation.DonationRequestManagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import daniarachid.donation.MainActivity;
import daniarachid.donation.Messaging.Conversation;
import daniarachid.donation.Notification.APISERVICE;
import daniarachid.donation.Notification.Client;
import daniarachid.donation.Notification.Data;
import daniarachid.donation.Notification.Response;
import daniarachid.donation.Notification.Sender;
import daniarachid.donation.Notification.Token;
import daniarachid.donation.R;
import retrofit2.Call;
import retrofit2.Callback;

public class DonorDonationRequest extends AppCompatActivity {
    FirebaseFirestore fStore;
    ImageView imgItem;
    TextView mTitle, mQuantity, mDate, mStatus;
    Button btnApprove, btnReject;
    FloatingActionButton messageDonor;
    String requestId, title, itemId, requestDate, donorId, receiverId, quantity, category;

    String requestStatus;

    ListenerRegistration registration;
    //for notification
    String nameOfSender, token, userIdForToken;
    APISERVICE apiservice;
    boolean notify = false;




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
        messageDonor = findViewById(R.id.btnSendMessage);

        apiservice = Client.getRetrofit("https://fcm.googleapis.com/").create(APISERVICE.class);

        // retrieve data from intent
        displayRequestDetails();

        btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                requestStatus = "Approved";
                //update status of request
                DocumentReference df = fStore.collection("DonationRequest").document(requestId);
                Map<String, Object> editedRequest = new HashMap<>();
                editedRequest.put("donorId", donorId);
                editedRequest.put("itemId", itemId);
                editedRequest.put("quantity", quantity);
                editedRequest.put("receiverId", receiverId);
                editedRequest.put("requestDate", requestDate);
                editedRequest.put("requestStatus", requestStatus);
                df.update(editedRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        notify = true;
                        Toast.makeText(getApplicationContext(), "Request has been approved", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                //notify user
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                String userId = fAuth.getCurrentUser().getUid();
                fStore.collection("Users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        nameOfSender = documentSnapshot.getString("name");
                        if (notify) {
                            sendRequestStatusNotification(receiverId, nameOfSender);


                        }

                        notify = false;
                    }
                });

                finish();
                startActivity(new Intent(getApplicationContext(), TestDonorRequestList.class));


            }
        });

        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStatus = "Rejected";
                notify = true;
                //update status of request
                DocumentReference df = fStore.collection("DonationRequest").document(requestId);
                Map<String, Object> editedRequest = new HashMap<>();
                editedRequest.put("donorId", donorId);
                editedRequest.put("itemId", itemId);
                editedRequest.put("quantity", quantity);
                editedRequest.put("receiverId", receiverId);
                editedRequest.put("requestDate", requestDate);
                editedRequest.put("requestStatus", requestStatus);
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

                //notify user
                //notify user
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                String userId = fAuth.getCurrentUser().getUid();
                fStore.collection("Users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        nameOfSender = documentSnapshot.getString("name");
                        if (notify) {
                            sendRequestStatusNotification(receiverId, nameOfSender);


                        }

                        notify = false;
                    }
                });


                finish();
                startActivity(new Intent(getApplicationContext(), TestDonorRequestList.class));
                //notify user

            }
        });

        messageDonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), Conversation.class);
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                String userId = fAuth.getCurrentUser().getUid();
                intent.putExtra("senderId", userId);
                intent.putExtra("receiverId", receiverId);
                //Log.d("CheckMe", donorId);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
            }
        });
    }

    private void sendRequestStatusNotification(String receiverId, String nameOfSender) {

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        userIdForToken = fAuth.getCurrentUser().getUid();

        DocumentReference df = fStore.collection("Tokens").document(receiverId);
                registration = df.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                //get the token
                assert value != null;
                Token objectToken = value.toObject(Token.class);
                assert objectToken != null;
                token = objectToken.getToken();
                Log.d("CheckMe", "Token from request view --> " + token);


                String message, notifTitle;
                if (requestStatus == "Approved") {
                    message = "has accepted your donation request";
                    notifTitle ="Your donation request has been accepted";
                } else{
                    message = "has rejected your donation request";
                    notifTitle ="Your donation request has been rejected";
                }
                //set the data for the notification
                Data data = new Data(donorId,  R.drawable.notification,
                        nameOfSender + " " +message,  notifTitle , receiverId, requestId, title, 3);
                Log.d("CheckMe", "Request ID" + requestId);
                Log.d("CheckMe", "Title" + title);
                Log.d("CheckMe", "donor ID" + donorId);


                Sender sender = new Sender(data, token);
                apiservice.sendNotification(sender).enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        Toast.makeText(getApplicationContext(), ""+response.message(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {

                    }
                });


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

    @Override
    protected void onStop() {
        super.onStop();
        if(registration != null) {
            registration.remove();
        }
    }

    public void signout() {

        FirebaseAuth.getInstance().signOut();


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

            case R.id.itemCollected:
                confirmCollection();

                break;

            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmCollection() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm of completing this item donation?");
        builder.setMessage("confirming this means completion of donation process and receiver collection of the donated item. Do you confirm?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                moveRequestToHistory();
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

    private void moveRequestToHistory() {
        //remove from donationrequest
        /*
        DocumentReference doc = fStore.collection("DonationRequest").document(requestId);
        doc.delete();
\

         */
        //instead of creating a new document ==> update request status to completed

        DocumentReference df = fStore.collection("DonationRequest").document(requestId);
        Map<String, Object> updatedRequest = new HashMap<>();
        updatedRequest.put("requestStatus", "Donated");
        df.update(updatedRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });

        /*
        //add to history
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String strDate = df.format(date);
        DocumentReference document = fStore.collection("DonationRequestHistory").document();
        Map<String, Object> request = new HashMap<>();
        request.put("requestId", requestId);
        request.put("donorId", donorId);
        request.put("receiverId", receiverId);
        request.put("collectionDate", strDate);
        request.put("itemId", itemId);
        request.put("quantity", quantity);
        document.set(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Request has been completed and moved to history", Toast.LENGTH_SHORT).show();

            }
        });


         */
        DocumentReference documentReference = fStore.collection("Items").document(itemId);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                category = documentSnapshot.get("category").toString();

                //archive item
                DocumentReference document = fStore.collection("ArchivedItems").document();
                Map<String, Object> item = new HashMap<>();
                item.put("itemId", itemId);
                item.put("title", title);
                item.put("userId", donorId);
                item.put("category", category);
                document.set(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("TAG", "item archived");
                    }
                });
            }
        });


        finish();
        startActivity(new Intent(this, TestDonorRequestList.class));
    }
}