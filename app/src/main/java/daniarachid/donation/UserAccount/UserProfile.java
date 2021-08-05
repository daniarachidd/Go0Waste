package daniarachid.donation.UserAccount;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import daniarachid.donation.Administration.AdminControl;
import daniarachid.donation.Administration.ContactUs;
import daniarachid.donation.Administration.MainReport;
import daniarachid.donation.DonationManagement.MainDonation;
import daniarachid.donation.DonationManagement.TestMyItem;
import daniarachid.donation.DonationRequestManagement.DonationRequestHistory;
import daniarachid.donation.DonationRequestManagement.TestDonorRequestList;
import daniarachid.donation.DonationRequestManagement.TestReceiverRequestList;
import daniarachid.donation.Messaging.Chat;
import daniarachid.donation.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView mName, mEmail, mPhone, mAddress, verifyMsg;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    String userId, authorized;
    CircleImageView profileImg;
    public DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        setNavigationDrawer();

        mName = findViewById(R.id.txtFullName);
        mEmail  = findViewById(R.id.txtEmail);
        mPhone = findViewById(R.id.txtPhone);
        //mAddress = findViewById(R.id.txtAddress);
        verifyMsg = findViewById(R.id.verifyMsg);

        profileImg = findViewById(R.id.imgProfile);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();

        final FirebaseUser user = fAuth.getCurrentUser();
        // CHECK IF USER EMAIL IS VERIFIED
        if(!user.isEmailVerified()) {
            verifyMsg.setVisibility(View.VISIBLE);
            verifyMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(view.getContext(), "Verification email has been sent", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Log.d("TAG", "onFailure: verification email is not sent " + e.getMessage());

                        }
                    });
                }
            });
        }





        //DISPLAY PROFILE IMAGE
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference userImage = storageReference.child("Users/" +fAuth.getCurrentUser().getUid() + "profile.jpg");
        userImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImg);
            }
        });

        //DISPLAY USER DETAILS
        DocumentReference docReference = fStore.collection("Users").document(userId);
        docReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot documentSnapshot, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                mPhone.setText(documentSnapshot.getString("phone"));
                mName.setText(documentSnapshot.getString("name"));
                mEmail.setText(documentSnapshot.getString("email"));
            }
        });

        //SET PROFILE IMAGE
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // OPEN GALLERY
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);


            }
        });



    }

    private void setNavigationDrawer() {

        // set the picture here
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.constraint_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        //authorize user
        DocumentReference document =  fStore.collection("Users").document(userId);
        document.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()) {
                        authorized = doc.get("isAuthorized").toString();
                        if (authorized=="true") {
                            Log.d("CheckMe", "authorized");
                            navigationView.getMenu().clear();
                            navigationView.inflateMenu(R.menu.nav_menu_admin);

                        }
                    }
                }
            }
        });



        ImageView nav_profileImg = headerView.findViewById(R.id.nav_user_image);

        //DISPLAY PROFILE IMAGE
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("Users/" +fAuth.getCurrentUser().getUid() + "profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(nav_profileImg);
            }
        });

        nav_profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UserProfile.class));
            }
        });


        //display name and email
        TextView mUsername = headerView.findViewById(R.id.username);
        TextView mEmail = headerView.findViewById(R.id.useremail);

        //String username, email;
        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()) {
                    mUsername.setText(doc.get("name").toString());
                    mEmail.setText(doc.get("email").toString());
                }
            }
        });

        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_donatoinItems:
                startActivity(new Intent(getApplicationContext(), MainDonation.class));
                break;
            case R.id.nav_myItems:
                startActivity(new Intent(getApplicationContext(), TestMyItem.class));
                break;
            case R.id.nav_outgoing:
                startActivity(new Intent(getApplicationContext(), TestReceiverRequestList.class));
                break;
            case R.id.nav_incoming:
                startActivity(new Intent(getApplicationContext(), TestDonorRequestList.class));
                break;
            case R.id.nav_history:
                startActivity(new Intent(getApplicationContext(), DonationRequestHistory.class));
                break;
            case R.id.nav_chat:
                startActivity(new Intent(getApplicationContext(), Chat.class));
                break;
            case R.id.nav_contactUs:
                startActivity(new Intent(getApplicationContext(), ContactUs.class));
                break;
            case R.id.nav_adminsControl:
                startActivity(new Intent(getApplicationContext(), AdminControl.class));
                break;
            case R.id.nav_report:
                startActivity(new Intent(getApplicationContext(), MainReport.class));
                break;
            case R.id.nav_signout:
                signout();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        // if the drawer is on the right size of the scrren --> GravityCompant.END
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== 1000) {
            if(resultCode == Activity.RESULT_OK) {
                // GET URI OF IMAGE
                Uri imageUri = data.getData();
                //profileImg.setImageURI(imageUri);

                // UPLOAD IMAGE TO FIREBASE
                uploadImageToFirebase(imageUri);

            }

        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        //UPLOAD IMAGE TO FIREBASE STORAGE
        StorageReference fileRef = storageReference.child("Users/" +fAuth.getCurrentUser().getUid() + "profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //get the downloadable uri
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImg);
                    }
                });

                Toast.makeText(UserProfile.this, "Image uploaded", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(UserProfile.this, "Failed", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void goToUpdate(View view) {
        String name = mName.getText().toString();
        String phone = mPhone.getText().toString();
        String email = mEmail.getText().toString();
        Intent intent = new Intent(view.getContext(), EditProfile.class);
        intent.putExtra("name", name);
        intent.putExtra("phone", phone);
        intent.putExtra("email", email);
        startActivity(intent);
    }


    public void signout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();;

    }



}