package daniarachid.donation.DonationManagement;

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
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
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
import daniarachid.donation.DonationRequestManagement.TestReceiverRequestList;
import daniarachid.donation.MainActivity;
import daniarachid.donation.R;
import daniarachid.donation.DonationRequestManagement.TestDonorRequestList;
import daniarachid.donation.UserAccount.UserProfile;

public class DonationItemView extends AppCompatActivity implements  NumberPicker.OnValueChangeListener{
    String itemId, title, category, quantity, description, userId, strImgUri, donor;
    Uri imageUri;
    ImageView itemImage, imgDelete;
    TextView mTitle, mDesc, mQuan, mStatus, mPick, mCategory, mSelectedQuantity;
    //NumberPicker quantityPicker;
    Button btnRequest;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    int selectedQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_item_view);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Map<String, Object> item = (Map<String, Object>) getIntent().getSerializableExtra("item");
        itemId = (String) item.get("itemId");
        title = (String) item.get("title");
        category = (String) item.get("category");
        quantity = (String) item.get("quantity");
        description = (String) item.get("description");
        //donor = (String) item.get("donor");

        donor = (String) item.get("donorId");


        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        itemImage = findViewById(R.id.imgItem);
        mTitle = findViewById(R.id.txtTitle);
        mDesc = findViewById(R.id.txtDesc);
        mQuan = findViewById(R.id.txtQuantity);
        mCategory = findViewById(R.id.txtRequestDate);
        mStatus = findViewById(R.id.txtStatus);
        btnRequest = findViewById(R.id.btnCancelRequest);
       // quantityPicker = findViewById(R.id.quantityPicker);
        mPick = findViewById(R.id.pickQuantity);
        mSelectedQuantity = findViewById(R.id.txtSelectedQuantity);

        int quantityVal = Integer.parseInt(quantity);
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



        showDetails();
    }


    private void requestItem(View v) {

      // int selectedQuantity = quantityPicker.getValue();
        // add a donation request to firebase
        //userId, donorId, donationRequestId, itemId, quantity, date
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

            case R.id.userProfile:
                startActivity(new Intent(getApplicationContext(), UserProfile.class));
                break;
            case R.id.donationRequestsRec:
                startActivity(new Intent(getApplicationContext(), TestReceiverRequestList.class));
                break;
            case R.id.receivedDonationRequests:
                startActivity(new Intent(getApplicationContext(), TestDonorRequestList.class));
                break;
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
        mStatus.setText("Available");
        mCategory.setText(category);


        if (quantity.equals("1")) {
            mPick.setVisibility(View.GONE);
            mSelectedQuantity.setVisibility(View.GONE);
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
        StorageReference profileRef = storageReference.child("Items/" + itemId + "-" + title + ".jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(itemImage);
            }

        });

    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        //get the value
        selectedQuantity = picker.getValue();
        mSelectedQuantity.setText(selectedQuantity);

    }

    public void showNumberPicker(View view){
        //Log.d("CheckMe", quantity);
        int max = Integer.parseInt(quantity);
        QuantityPickerDialog newFragment = new QuantityPickerDialog(1, max);
        newFragment.setValueChangeListener(this);

        newFragment.show(getSupportFragmentManager(), "Quantity Picker");

        //newFragment.setValueChangeListener(this);
        //newFragment.show(getSupportFragmentManager(), "time picker");
    }
}