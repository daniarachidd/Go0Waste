package daniarachid.donation.DonationRequestManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import daniarachid.donation.Adapters.ReceiverRequestsAdapter;
import daniarachid.donation.Administration.AdminControl;
import daniarachid.donation.Administration.ContactUs;
import daniarachid.donation.Administration.MainReport;
import daniarachid.donation.DonationManagement.MainDonation;
import daniarachid.donation.DonationManagement.TestMyItem;
import daniarachid.donation.UserAccount.MainActivity;
import daniarachid.donation.Messaging.Chat;
import daniarachid.donation.R;
import daniarachid.donation.UserAccount.UserProfile;


public class TestDonorRequestList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    RecyclerView penRequestList, resRequestList;
    List<String> statusList, requestIds, itemIds, images, donorIds, quantity;
    List<String> penStatusList, penRequestIds, penItemIds, penImages, penDonorIds, penQuantity;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    int itemCount = 0;
    ReceiverRequestsAdapter pendingAdapter, respondedAdapter;
    public DrawerLayout drawer;
    String userId, authorized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_donor_request_list);

        setNavigationDrawer();

        //initialize views
        resRequestList = findViewById(R.id.respondedRequests);
        penRequestList = findViewById(R.id.pendingRequests);


        //initialize firebase connections
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        //initialize arraylists

        requestIds = new ArrayList<>();
        itemIds = new ArrayList<>();
        images = new ArrayList<>();
        donorIds = new ArrayList<>();
        quantity = new ArrayList<>();
        statusList = new ArrayList<>();

        penRequestIds =new ArrayList<>();
        penItemIds = new ArrayList<>();
        penImages = new ArrayList<>();
        penDonorIds = new ArrayList<>();
        penQuantity = new ArrayList<>();
        penStatusList = new ArrayList<>();

        displayRequests();

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

    private void displayRequests() {
        String donorId = fAuth.getCurrentUser().getUid();

        fStore.collection("DonationRequest").whereEqualTo("donorId", donorId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()) {
                           // Toast.makeText(getApplicationContext(), "There are no requests", Toast.LENGTH_SHORT).show();

                        }


                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                Toast.makeText(getApplicationContext(), "No requests", Toast.LENGTH_SHORT).show();
                            }

                            //adding the requests to array lists
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                //if () if status is requested --> pending
                                if (doc.getString("requestStatus").equals("Requested")) {
                                    penRequestIds.add(doc.getId());
                                    penItemIds.add(doc.get("itemId").toString());
                                    penDonorIds.add(doc.get("donorId").toString());
                                    penQuantity.add(doc.get("quantity").toString());
                                    penStatusList.add(doc.get("requestStatus").toString());

                                } else if (doc.getString("requestStatus").equals("Approved") ||
                                doc.getString("requestStatus").equals("Rejected")){
                                    itemCount ++;
                                    requestIds.add(doc.getId());
                                    itemIds.add(doc.get("itemId").toString());
                                    donorIds.add(doc.get("donorId").toString());
                                    quantity.add(doc.get("quantity").toString());
                                    statusList.add(doc.get("requestStatus").toString());
                                }



                            }



                            //attach with the adapter
                            pendingAdapter = new ReceiverRequestsAdapter(getApplicationContext(), penRequestIds, penDonorIds,penItemIds, penStatusList);
                            respondedAdapter = new ReceiverRequestsAdapter(getApplicationContext(), requestIds, donorIds, itemIds, statusList);

                            GridLayoutManager gLManager = new GridLayoutManager(getApplicationContext(), 1,
                                    GridLayoutManager.VERTICAL, false);

                            GridLayoutManager penGLManager = new GridLayoutManager(getApplicationContext(), 1,
                                    GridLayoutManager.VERTICAL, false);
                            //LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());

                            // requestList.setLayoutManager(llm);
                            resRequestList.setLayoutManager(gLManager);
                            resRequestList.setAdapter(respondedAdapter);

                            penRequestList.setLayoutManager(penGLManager);
                            penRequestList.setAdapter(pendingAdapter);
                            if (penItemIds.size() == 0 && itemIds.size() > 0 ) {
                                TextView resTextView = findViewById(R.id.txtPending);
                                resTextView.setVisibility(View.GONE);
                                 penRequestList.setVisibility(View.GONE);
                            }
                            if (itemIds.size() == 0 && penItemIds.size() > 0 ){
                                TextView resTextView = findViewById(R.id.txtRequests);
                                resTextView.setVisibility(View.GONE);
                                resRequestList.setVisibility(View.GONE);
                            }

                            if (itemIds.size() == 0 && penItemIds.size() == 0) {
                                TextView pending = findViewById(R.id.txtPending);
                                TextView resTextView = findViewById(R.id.txtRequests);
                                pending.setText("There are no requests");
                                resTextView.setVisibility(View.GONE);

                            }



                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {

            }
        });
    }

    /*

    //HANDLE OPTION MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.searchIcon);
        return true;
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

     */

    public void signout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();

    }
}