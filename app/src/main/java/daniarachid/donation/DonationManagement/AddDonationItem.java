package daniarachid.donation.DonationManagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import daniarachid.donation.MainActivity;
import daniarachid.donation.R;

public class AddDonationItem extends AppCompatActivity {
    EditText mTitle, mDesc, mQuantity;
    Button btnAdd, btnCancel;
    ImageView itemImg;
    Spinner spinCategory;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    String userId, imgUri, itemId;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_donation_item);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mTitle = findViewById(R.id.txtItemTitle);
        mDesc    = findViewById(R.id.txtItemDesc);
        mQuantity = findViewById(R.id.txtQuantity);
        spinCategory = findViewById(R.id.spinCategory);
        btnAdd = findViewById(R.id.btnAdd);
        btnCancel = findViewById(R.id.btnCancel);
        itemImg = findViewById(R.id.itemImg);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userId = fAuth.getCurrentUser().getUid();

        String[] categories = {"Food", "Women Clothes", "Men Clothes", "Kids Clothes", "Toys", "Appliances"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddDonationItem.this,android.R.layout.simple_spinner_item, categories);
        spinCategory.setAdapter(adapter);
        adapter.notifyDataSetChanged();



        //OPEN GALLERY
        itemImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainDonation.class));
                finish();
            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== 1000) {
            if(resultCode == Activity.RESULT_OK) {
                // GET URI OF IMAGE
                imageUri = data.getData();
                itemImg.setImageURI(imageUri);
                imgUri = imageUri.toString();

                // UPLOAD IMAGE TO FIREBASE
               //

            }

        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        //UPLOAD IMAGE TO FIREBASE STORAGE
        StorageReference fileRef = storageReference.child("Items/" + itemId + "-" + mTitle.getText().toString() +".jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //get the downloadable uri
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(itemImg);
                    }
                });

                Toast.makeText(AddDonationItem.this, "Image uploaded", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(AddDonationItem.this, "Failed", Toast.LENGTH_SHORT).show();

            }
        });

    }



    public void addItem(View view) {
        String itemTitle = mTitle.getText().toString();
        String itemDesc = mDesc.getText().toString();
        String itemQuan = mQuantity.getText().toString();
        String category = spinCategory.getSelectedItem().toString();


        int quantity = Integer.parseInt(itemQuan);
        if (quantity <= 0) {
            mQuantity.setError("Quantity must be > 0");
            return;
        }
        if (TextUtils.isEmpty(itemQuan)) {
            mQuantity.setError("Enter quantity");
            return;
        }
        if (TextUtils.isEmpty(itemTitle)) {
        mTitle.setError("Enter item title");
        return;
        }

        if (TextUtils.isEmpty(itemDesc)) {
            mDesc.setError("Enter item description");
            return;
        }

        userId = fAuth.getCurrentUser().getUid();
        //Create an object of Product
        DocumentReference docReference = fStore.collection("Items").document();
        itemId = docReference.getId();

        if (imageUri == null) {
            Toast.makeText(getApplicationContext(), "Please upload an image", Toast.LENGTH_SHORT).show();
            return;
        }
        //Insert the Image to the fire storage
        uploadImageToFirebase(imageUri);

        //get today date
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String currentTime = formatter.format(date);
        Map<String, Object> item = new HashMap<>();

        item.put("title", itemTitle);
        item.put("description", itemDesc);
        item.put("quantity", itemQuan);
        item.put("category", category);
        item.put("userId", userId);
        item.put("image", imgUri);
        item.put("postedDate", currentTime);
        item.put("status", "Available");
        docReference.set(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(AddDonationItem.this, "Item added", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainDonation.class));
                finish();
            }
        });


    }


    //HANDLE OPTION MENU

    public void signout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();;

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {

            case android.R.id.home:
                this.finish();
                return true;



        }
        return super.onOptionsItemSelected(item);
    }


}