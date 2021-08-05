package daniarachid.donation.DonationManagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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

import daniarachid.donation.DonationRequestManagement.QuantityPickerDialog;
import daniarachid.donation.UserAccount.MainActivity;
import daniarachid.donation.Notification.APISERVICE;
import daniarachid.donation.Notification.Client;
import daniarachid.donation.Notification.Data;
import daniarachid.donation.Notification.Response;
import daniarachid.donation.Notification.Sender;
import daniarachid.donation.Notification.Token;
import daniarachid.donation.R;
import retrofit2.Call;
import retrofit2.Callback;

public class DonationItemView extends AppCompatActivity implements  NumberPicker.OnValueChangeListener{
    String itemId, title, category, quantity, description, userId, donor;
    ImageView itemImage;
    TextView mTitle, mDesc, mQuan, mPick, mCategory, mSelectedQuantity;
    Button btnRequest;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String requestId;

    int selectedQuantity;
    int quantityVal;


    //for notification
    String nameOfSender, token, userIdForToken;
    APISERVICE apiservice;
    boolean notify = false;

    boolean userStatus = true;
    ListenerRegistration registration;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_item_view);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);



        apiservice = Client.getRetrofit("https://fcm.googleapis.com/").create(APISERVICE.class);


        Map<String, Object> item = (Map<String, Object>) getIntent().getSerializableExtra("item");
        itemId = (String) item.get("itemId");
        title = (String) item.get("title");
        category = (String) item.get("category");
        quantity = (String) item.get("quantity");
        description = (String) item.get("description");
        donor = (String) item.get("donorId");


        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        itemImage = findViewById(R.id.imgItem);
        mTitle = findViewById(R.id.txtTitle);
        mDesc = findViewById(R.id.txtDesc);
        mQuan = findViewById(R.id.txtQuantity);
        mCategory = findViewById(R.id.txtRequestDate);

        btnRequest = findViewById(R.id.btnCancelRequest);
        mPick = findViewById(R.id.pickQuantity);
        mSelectedQuantity = findViewById(R.id.txtSelectedQuan);

         quantityVal = Integer.parseInt(quantity);


        showDetails();
        mPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create the dialog
                showNumberPicker(mPick);

            }
        });


        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                requestItem(btnRequest);
            }
        });




    }


    private void requestItem(View v) {
        notify = true;

        if (selectedQuantity == 0) {
            selectedQuantity = quantityVal;
        }
        // add a donation request to firebase

        Map<String, Object> donationRequest = new HashMap<>();
        donationRequest.put("receiverId", fAuth.getCurrentUser().getUid());
        donationRequest.put("donorId", donor);
        donationRequest.put("itemId", itemId);
        donationRequest.put("quantity", selectedQuantity);

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String strDate = df.format(date);
        donationRequest.put("requestDate", strDate);
        donationRequest.put("requestStatus", "Requested");

        DocumentReference doc = fStore.collection("DonationRequest").document();
        doc.set(donationRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Request is sent", Toast.LENGTH_SHORT).show();
                requestId = doc.getId();

                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(DonationItemView.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });





        //disable request button
        btnRequest.setEnabled(false);
        btnRequest.setText("Requested");

        fStore.collection("Users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                nameOfSender = documentSnapshot.getString("name");
                if (notify) {
                    sendRequestNotification(donor, nameOfSender);
                    //Log.d("CheckMe", "Notify: " + nameOfSender);

                }

                notify = false;
            }
        });






    }


    @Override
    protected void onStop() {

        if(registration != null) {
            registration.remove();
        }

        super.onStop();
    }

    private void sendRequestNotification(String donor, String nameOfSender) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        userIdForToken = fAuth.getCurrentUser().getUid();
        Log.d("ChekMe", "Donor Id "  + donor);
        DocumentReference df = fStore.collection("Tokens").document(donor);
        registration = df.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (userStatus == false) {

                }
                assert value != null;
                Token objectToken = value.toObject(Token.class);
                assert objectToken != null;
                token = objectToken.getToken();
               Log.d("CheckMe", "Token: " + token);

                //String message = "";
                Data data = new Data(userId,  R.drawable.notification,
                         nameOfSender + " has requested your item " + title, "New Donation Request", donor, requestId, title, 2);


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


    public void signout() {
        userStatus = false;
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }






    private void showDetails() {
        mTitle.setText(title);
        mDesc.setText(description);
        mQuan.setText(quantity);
        mCategory.setText(category);



        if (quantityVal == 1) {
            mPick.setVisibility(View.GONE);
            mSelectedQuantity.setVisibility(View.GONE);
            selectedQuantity = 1;
        }

        //check if the item is already requested
        fStore.collection("DonationRequest").whereEqualTo("receiverId", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
               //if the found item id = the clicked item id
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    if (itemId.equals(doc.getString("itemId"))) {

                        mPick.setVisibility(View.GONE);
                        mSelectedQuantity.setVisibility(View.GONE);
                        btnRequest.setEnabled(false);
                        btnRequest.setText("Requested");
                    }
                }
            }
        });

        //if the item is the user's item
        fStore.collection("Items").whereEqualTo("userId", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    if(donor.equals(doc.getString("userId"))) {
                        btnRequest.setVisibility(View.GONE);
                        mPick.setVisibility(View.GONE);
                        mSelectedQuantity.setVisibility(View.GONE);
                    }
                }
            }
        });

        //display pic
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("Items/" + itemId + ".jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(itemImage);
            }

        });

    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

        selectedQuantity = picker.getValue();
        mSelectedQuantity.setVisibility(View.VISIBLE);
        quantity = String.valueOf(selectedQuantity);
        mSelectedQuantity.setText(quantity);


    }

    public void showNumberPicker(View view){

        int max = quantityVal;
        QuantityPickerDialog newFragment = new QuantityPickerDialog(1, max);
        newFragment.setValueChangeListener(this);
        newFragment.show(getSupportFragmentManager(), "Quantity Picker");

    }
}