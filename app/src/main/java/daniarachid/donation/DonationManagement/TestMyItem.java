package daniarachid.donation.DonationManagement;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import daniarachid.donation.Adapters.MyItemsAdapter;
import daniarachid.donation.Administration.AdminControl;
import daniarachid.donation.Administration.ContactUs;
import daniarachid.donation.Administration.MainReport;
import daniarachid.donation.DonationRequestManagement.DonationRequestHistory;
import daniarachid.donation.DonationRequestManagement.TestDonorRequestList;
import daniarachid.donation.DonationRequestManagement.TestReceiverRequestList;
import daniarachid.donation.UserAccount.MainActivity;
import daniarachid.donation.Messaging.Chat;
import daniarachid.donation.R;
import daniarachid.donation.UserAccount.UserProfile;

public class TestMyItem extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView itemList;
    List<String> titles, productId, categories;
    List<String> images;
    List<String> descriptions;
    List<String> quantities;
    List<String> userIds;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    SearchView searchView;
    String userId, authorized;
    int itemCount = 0;
    MyItemsAdapter adapter;
    TextView txtEmpty;
    FloatingActionButton fabAdd;

    public DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_my_item);


        setNavigationDrawer();
        itemList = findViewById(R.id.itemList);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();



        categories = new ArrayList<>();
        userIds = new ArrayList<>();
        productId = new ArrayList<>();
        titles = new ArrayList<>();
        images = new ArrayList<>();
        descriptions = new ArrayList<>();
        quantities = new ArrayList<>();



        fabAdd = (FloatingActionButton) findViewById(R.id.fabAddItem);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddDonationItem.class));
            }
        });

        //RETRIEVE THE DATA
        retrieve();


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
    public void retrieve(){


        fStore.collection("Items").whereEqualTo("userId", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().isEmpty()) {
                        //Toast.makeText(getApplicationContext(), "there are no items", Toast.LENGTH_SHORT).show();
                        txtEmpty = findViewById(R.id.txtEmptyItems);
                        itemList.setVisibility(View.GONE);
                        txtEmpty.setText("You haven't added any item yet");
                        txtEmpty.setVisibility(View.VISIBLE);

                    }
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        itemCount ++;
                        //images.add( document.get("image").toString());
                        //images.add("Items/" + document.getId() + "-" + document.get("title").toString() + ".jpg");
                        //Log.d("Image Uri" , "Items/" + document.getId() + "-" + document.get("title").toString() + ".jpg");
                        titles.add(document.get("title").toString());
                        descriptions.add(document.get("description").toString());
                        quantities.add(document.get("quantity").toString());
                        categories.add(document.get("category").toString());
                        productId.add(document.getId());
                        userIds.add(document.get("userId").toString());


                       //Log.d("TAG", document.getId() + " => " + document.getData());
                      //  Log.d("TAG", "%%%%%%%%List Titles " + document.get("title"));
                    }
                    //Log.d("TAG", "Total Number of items = " + itemCount);
                    //create adapter
                    adapter = new MyItemsAdapter(getApplicationContext(), titles, images , descriptions, quantities, categories, productId, userIds);

                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2, GridLayoutManager.VERTICAL, false);
                    itemList.setLayoutManager(gridLayoutManager);
                    itemList.setAdapter(adapter);

                    // on click



                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }



            }
        });


    }





    public void searchData(String query) {

        fStore.collection("Items").whereEqualTo("title", query).whereEqualTo("userId", fAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            //clear the recyclerview
                            itemList.removeAllViews();
                            itemCount = 0;
                            titles.clear();
                            descriptions.clear();
                            quantities.clear();
                            categories.clear();
                            productId.clear();
                            userIds.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                itemCount ++;
                                titles.add(document.get("title").toString());
                                descriptions.add(document.get("description").toString());
                                quantities.add(document.get("quantity").toString());
                                categories.add(document.get("category").toString());
                                productId.add(document.getId());
                                userIds.add(document.get("userId").toString());
                            }

                            adapter = new MyItemsAdapter(getApplicationContext(), titles, images , descriptions, quantities, categories, productId, userIds);
                            GridLayoutManager gLManager = new GridLayoutManager(getApplicationContext(), 2, GridLayoutManager.VERTICAL, false);
                            itemList.setLayoutManager(gLManager);
                            itemList.setAdapter(adapter);


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void signout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();

    }


    /*

     */
    //HANDLE OPTION MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.searchIcon);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search Here!");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //do the search here
                return false;
            }
        });
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

}