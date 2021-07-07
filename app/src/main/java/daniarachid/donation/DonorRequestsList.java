package daniarachid.donation;

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

import daniarachid.donation.R;

public class DonorRequestsList extends AppCompatActivity {
    RecyclerView requestList;
    List<String> statusList, requestIds, itemIds, images, donorIds, quantity;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    int itemCount = 0;
    ReceiverRequestsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_requests_list);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //initialize views
        requestList = findViewById(R.id.donationRequestsRec);

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



        //display requests
        displayRequests();
    }

    private void displayRequests() {
        String donorId = fAuth.getCurrentUser().getUid();

        fStore.collection("DonationRequest").whereEqualTo("donorId", donorId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(getApplicationContext(), "There are no requests", Toast.LENGTH_SHORT).show();
                        }


                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                Toast.makeText(getApplicationContext(), "No requests", Toast.LENGTH_SHORT).show();
                            }

                            //adding the requests to array lists
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                itemCount ++;
                                requestIds.add(doc.getId());
                                itemIds.add(doc.get("itemId").toString());
                                donorIds.add(doc.get("donorId").toString());
                                quantity.add(doc.get("quantity").toString());
                                statusList.add(doc.get("requestStatus").toString());


                            }


                            //attach with the adapter
                            adapter = new ReceiverRequestsAdapter(getApplicationContext(), requestIds, donorIds, itemIds, statusList);

                            GridLayoutManager gLManager = new GridLayoutManager(getApplicationContext(), 1,
                                    GridLayoutManager.VERTICAL, false);
                            //LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());

                            // requestList.setLayoutManager(llm);
                            requestList.setLayoutManager(gLManager);
                            requestList.setAdapter(adapter);



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