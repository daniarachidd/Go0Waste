package daniarachid.donation.DonationManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import daniarachid.donation.Adapters.MainDonationViewAdapter;
import daniarachid.donation.Categories;
import daniarachid.donation.DonationRequestManagement.DonorRequestsList;
import daniarachid.donation.MainActivity;
import daniarachid.donation.R;
import daniarachid.donation.DonationRequestManagement.ReceiverRequestsList;
import daniarachid.donation.UserAccount.UserProfile;

public class TestMainDonation extends AppCompatActivity {

    RecyclerView itemList;
    List<String> titles, productId, categories, donors;
    List<String> images;
    List<String> descriptions;
    List<String> quantities;
    List<String> userIds;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    int itemCount = 0;
    MainDonationViewAdapter mAdapter;

    SearchView searchView;
    TextView txtEmpty;
    Button btnMyItems, btnCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_main_donation);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        itemList = findViewById(R.id.itemList);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();


        categories = new ArrayList<>();
        productId = new ArrayList<>();
        titles = new ArrayList<>();
        images = new ArrayList<>();
        descriptions = new ArrayList<>();
        quantities = new ArrayList<>();
        userIds = new ArrayList<>();

        btnMyItems = findViewById(R.id.btnMyItems);
        btnCategories = findViewById(R.id.btnCategories);
        btnCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Categories.class));
            }
        });



        btnMyItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TestMyItem.class));
            }
        });

        //RETRIEVE THE DATA
        retrieve();
    }


    public void retrieve(){


        fStore.collection("Items").whereNotEqualTo("userId", fAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //if there is no items to be displayed
                    if(task.getResult().isEmpty()) {
                        txtEmpty = findViewById(R.id.txtEmptyItems);
                        itemList.setVisibility(View.GONE);
                        txtEmpty.setText("You haven't added any item yet");
                        txtEmpty.setVisibility(View.VISIBLE);

                    }
                    //getting existing documents
                    for (QueryDocumentSnapshot document : task.getResult()) {
                            itemCount ++;
                            titles.add(document.get("title").toString());
                            descriptions.add(document.get("description").toString());
                            quantities.add(document.get("quantity").toString());
                            categories.add(document.get("category").toString());
                            productId.add(document.getId());
                            userIds.add(document.get("userId").toString());
                    }


                    //set the adapter
                    mAdapter = new MainDonationViewAdapter(getApplicationContext(), titles, images, descriptions, quantities, categories, productId, userIds);
                    GridLayoutManager gLManager = new GridLayoutManager(getApplicationContext(), 2, GridLayoutManager.VERTICAL, false);
                    itemList.setLayoutManager(gLManager);
                    itemList.setAdapter(mAdapter);

                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }



            }
        });


    }




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

    public void searchData(String query) {

        fStore.collection("Items").whereEqualTo("title", query).whereNotEqualTo("userId", fAuth.getCurrentUser().getUid()).get()
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
                    if(task.getResult() == null) {
                        //based on donor --> compare based on 
                    }
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        itemCount ++;
                        titles.add(document.get("title").toString());
                        descriptions.add(document.get("description").toString());
                        quantities.add(document.get("quantity").toString());
                        categories.add(document.get("category").toString());
                        productId.add(document.getId());
                        userIds.add(document.get("userId").toString());
                    }

                    mAdapter = new MainDonationViewAdapter(getApplicationContext(), titles, images, descriptions, quantities, categories, productId, userIds);
                    GridLayoutManager gLManager = new GridLayoutManager(getApplicationContext(), 2, GridLayoutManager.VERTICAL, false);
                    itemList.setLayoutManager(gLManager);
                    itemList.setAdapter(mAdapter);


                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getApplicationContext(), "There are not items found!", Toast.LENGTH_SHORT).show();

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.signout: signout();
                break;
            case R.id.search: //search
                break;
            case R.id.userProfile:
                startActivity(new Intent(getApplicationContext(), UserProfile.class));
                break;
            case R.id.donationRequestsRec:
                startActivity(new Intent(getApplicationContext(), ReceiverRequestsList.class));
                break;
            case R.id.receivedDonationRequests:
                startActivity(new Intent(getApplicationContext(), DonorRequestsList.class));
                break;
            case android.R.id.home:
                this.finish();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    public void signout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();

    }
}