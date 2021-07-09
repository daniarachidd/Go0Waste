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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
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
import daniarachid.donation.R;
import daniarachid.donation.UserAccount.UserProfile;

public class DonationItemView extends AppCompatActivity {
    String itemId, title, category, quantity, description, userId, strImgUri, donor;
    Uri imageUri;
    ImageView itemImage, imgDelete;
    TextView mTitle, mDesc, mQuan, mStatus, mDonor, mCategory;
    Button btnRequest;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

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


        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestItem(btnRequest);
            }
        });



        showDetails();
    }

    private void requestItem(View v) {
        // add a donation request to firebase
        //userId, donorId, donationRequestId, itemId, quantity, date
        Map<String, Object> donationRequest = new HashMap<>();
        donationRequest.put("receiverId", fAuth.getCurrentUser().getUid());
        donationRequest.put("donorId", donor);
        donationRequest.put("itemId", itemId);
        donationRequest.put("quantity", quantity);

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

        //SEND NOTIFICATION TO THE DONOR
        //MIGHT NEED TO CHECK THE QUANTITY






        /**
        int quan = Integer.parseInt(quantity);
        if(quan > 1) {
            //enter quantity required

            EditText mQuantity = new EditText(v.getContext());
            mQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
            AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
            passwordResetDialog.setTitle("Donation Request");
            passwordResetDialog.setMessage("Enter the quantity you want to request ");


            passwordResetDialog.setView(mQuantity);

            mQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    int requiredQuantity = Integer.parseInt(mQuantity.getText().toString());

                    if (requiredQuantity > quan || requiredQuantity < quan) {
                        //passwordResetDialog.setMessage("Available quantity is " + quantity);
                        //mQuantity.setFocusable(true);
                        //passwordResetDialog.setMultiChoiceItems()
                      //  multichoice
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {


                }
            });
            passwordResetDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {



                    Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
                }
            });
            passwordResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
                }
            });
            passwordResetDialog.create().show();



            //Toast.makeText(this, quantity, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Request has been sent to donor", Toast.LENGTH_SHORT).show();
        }
            **/

        //
            //ask user how many to request

        //what if quantity > 1>

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
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== 1000) {
            if(resultCode == Activity.RESULT_OK) {
                // GET URI OF IMAGE
                imageUri = data.getData();
                itemImage.setImageURI(imageUri);
                strImgUri = imageUri.toString();
                //profileImg.setImageURI(imageUri);

                // UPLOAD IMAGE TO FIREBASE
               // uploadImageToFirebase(imageUri);

            }

        }
    }

    **/
    private void showDetails() {
        mTitle.setText(title);
        mDesc.setText(description);
        mQuan.setText(quantity);
        mStatus.setText("Available");
        mCategory.setText(category);


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
}