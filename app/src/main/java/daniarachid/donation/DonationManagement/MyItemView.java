package daniarachid.donation.DonationManagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import daniarachid.donation.MainActivity;
import daniarachid.donation.R;

public class MyItemView extends AppCompatActivity {
    String itemId, title, category, quantity, description, userId, strImgUri;
    Uri imageUri;
    ImageView itemImage;
    EditText mTitle, mDesc, mQuan;
    Spinner mCategory;
    TextView mStatus;
    Switch swStatus;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;


    Button btnSave, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_item_view);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);



        Map<String, Object> item = (Map<String, Object>) getIntent().getSerializableExtra("item");
        itemId = (String) item.get("itemId");
        title = (String) item.get("title");
        category = (String) item.get("category");
        quantity = (String) item.get("quantity");
        description = (String) item.get("description");



        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        itemImage = findViewById(R.id.imgItem);
        mTitle = findViewById(R.id.txtTitle);
        mDesc = findViewById(R.id.txtDesc);
        mQuan = findViewById(R.id.txtQuantity);
        mCategory = findViewById(R.id.spinCategory);
        mStatus = findViewById(R.id.txtStatus);
        swStatus = findViewById(R.id.switchStatus);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);


        showDetails();

        //change item picture
        itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = mTitle.getText().toString();
                description = mDesc.getText().toString();
                quantity = mQuan.getText().toString();
                category = mCategory.getSelectedItem().toString();

                //Log.d("ItemInfo", title + " " + description + " " + quantity + " " + category);

                //upload changes
                DocumentReference docRef = fStore.collection("Items").document(itemId);
                Map<String, Object> editedItem = new HashMap<>();
                editedItem.put("title", title);
                editedItem.put("description", description);
                editedItem.put("quantity", quantity);
                editedItem.put("category", category);
                editedItem.put("userId", userId);
                editedItem.put("image", strImgUri);


                docRef.update(editedItem).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MyItemView.this, "Item details updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), TestMyItem.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(MyItemView.this, "Update Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TestMyItem.class));
                finish();
            }
        });



    }




    public  void deleteItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyItemView.this);
        builder.setTitle("Delete Donation Item?");
        builder.setMessage("This will permanently delete your item");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //delete
                DocumentReference docRef = fStore.collection("Items").document(itemId);
                docRef.delete();
                StorageReference storageReference;
                storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference fileRef = storageReference.child("Items/" +itemId + "-" + title + ".jpg");
                fileRef.delete();

                //delete related request
                fStore.collection("DonationRequest").whereEqualTo("itemId" , itemId)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty())
                            {
                                for(QueryDocumentSnapshot doc : task.getResult()) {
                                    DocumentReference dr = doc.getReference();
                                    dr.delete();
                                }
                            }
                        }
                    }
                });
                Toast.makeText(MyItemView.this, "Item is deleted", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), TestMyItem.class));
                finish();
                dialog.dismiss();
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
    private void showDetails() {
        mTitle.setText(title);
        mDesc.setText(description);
        mQuan.setText(quantity);
        mStatus.setText("Available");
        swStatus.setChecked(true);

        String[] categories = {"Food", "Women Clothes", "Men Clothes", "Kids Clothes", "Toys", "Appliances"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MyItemView.this,android.R.layout.simple_spinner_item, categories);
        mCategory.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        mCategory.setSelection(adapter.getPosition(category));

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
    public void signout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();;

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_delete, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteItem: deleteItem();
                break;

            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
                uploadImageToFirebase(imageUri);

            }

        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        //UPLOAD IMAGE TO FIREBASE STORAGE
        StorageReference storageReference;
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference fileRef = storageReference.child("Items/" +itemId + "-" + title + ".jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //get the downloadable uri
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(itemImage);
                    }
                });

                Toast.makeText(MyItemView.this, "Image uploaded", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(MyItemView.this, "Failed", Toast.LENGTH_SHORT).show();

            }
        });

    }




}