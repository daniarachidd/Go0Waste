package daniarachid.donation.DonationRequestManagement;

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

import daniarachid.donation.Adapters.ReceiverRequestsAdapter;
import daniarachid.donation.MainActivity;
import daniarachid.donation.R;
import daniarachid.donation.UserAccount.UserProfile;


public class TestDonorRequestList extends AppCompatActivity {
    RecyclerView penRequestList, resRequestList;
    List<String> statusList, requestIds, itemIds, images, donorIds, quantity;
    List<String> penStatusList, penRequestIds, penItemIds, penImages, penDonorIds, penQuantity;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    int itemCount = 0;
    ReceiverRequestsAdapter pendingAdapter, respondedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_donor_request_list);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

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
                            if (penItemIds.size() == 0 ) {
                                TextView resTextView = findViewById(R.id.txtPending);
                                resTextView.setVisibility(View.GONE);
                                 penRequestList.setVisibility(View.GONE);
                            }
                            if (itemIds.size() == 0) {
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
                startActivity(new Intent(getApplicationContext(), TestReceiverRequestList.class));
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