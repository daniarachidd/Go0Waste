package daniarachid.donation.Administration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import daniarachid.donation.Adapters.AdminsAdapter;
import daniarachid.donation.DonationManagement.MainDonation;
import daniarachid.donation.DonationManagement.TestMyItem;
import daniarachid.donation.DonationRequestManagement.DonationRequestHistory;
import daniarachid.donation.DonationRequestManagement.TestDonorRequestList;
import daniarachid.donation.DonationRequestManagement.TestReceiverRequestList;
import daniarachid.donation.Messaging.Chat;
import daniarachid.donation.R;
import daniarachid.donation.UserAccount.UserProfile;

public class AdminControl extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{
    FirebaseFirestore fStore;
    List<String> adminId, adminName, adminEmail;
    RecyclerView adminsList;
    AdminsAdapter adapter;
    EditText mEmail;
    Button btnAddAdmin;
    String newAdminId;

    public DrawerLayout drawer;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_control);

        fStore = FirebaseFirestore.getInstance();
        adminId = new ArrayList<>();
        adminName = new ArrayList<>();
        adminEmail = new ArrayList<>();
        adminsList = findViewById(R.id.adminsRecycler);
        mEmail = findViewById(R.id.txtAdminEmail);
        btnAddAdmin = findViewById(R.id.btnAddAdmin);
        btnAddAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAdmin();
            }
        });

        setNavigationDrawer();
        getAdmins();


    }


    private void setNavigationDrawer() {
        //navigation drawer setting

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = findViewById(R.id.constraint_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        ImageView nav_profileImg = headerView.findViewById(R.id.nav_user_image);
        // set the picture here
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
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


    public  void signout(){}
    private void addAdmin() {
        String email = mEmail.getText().toString();

        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Pleae enter the user email");
            return;
        } else {
            fStore.collection("Users").whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                if (email.equals(doc.get("email"))) {
                                    newAdminId = doc.getId();;
                                }
                            }

                            fStore.collection("Users").document(newAdminId)
                                    .update("isAuthorized", true)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                            Toast.makeText(getApplicationContext(), "Admin has been added", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), AdminControl.class));
                                        }
                                    });
                        }
                        else {
                            mEmail.setError("User not found");
                            return;
                        }
                    }
                }
            });
        }
    }

    public void getAdmins() {

        fStore.collection("Users").whereEqualTo("isAuthorized", true)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {

                        for (QueryDocumentSnapshot doc  : task.getResult()) {
                            adminId.add(doc.getId());
                            adminName.add(doc.get("name").toString());
                            adminEmail.add(doc.get("email").toString());
                        }

                        adapter = new AdminsAdapter(getApplicationContext(), adminId, adminName, adminEmail);
                        GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), 1,
                                GridLayoutManager.VERTICAL, false);
                        adminsList.setLayoutManager(manager);
                        adminsList.setAdapter(adapter);


                    }
                }
            }
        });
    }
}