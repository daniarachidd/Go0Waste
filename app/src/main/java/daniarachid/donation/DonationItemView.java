package daniarachid.donation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Map;

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
        donor = (String) item.get("donor");



        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        itemImage = findViewById(R.id.imgItem);
        mTitle = findViewById(R.id.txtTitle);
        mDesc = findViewById(R.id.txtDesc);
        mQuan = findViewById(R.id.txtQuantity);
        mCategory = findViewById(R.id.txtCategory);
        mStatus = findViewById(R.id.txtStatus);
        btnRequest = findViewById(R.id.btnRequest);


        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestItem();
            }
        });



        showDetails();
    }

    private void requestItem() {
        // add a donation request to firebase
        //userId, donorId, status, donationRequestId, date
        //what if quantity > 1> 
        //send a notification to the donor
        //change the button text to Requested
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