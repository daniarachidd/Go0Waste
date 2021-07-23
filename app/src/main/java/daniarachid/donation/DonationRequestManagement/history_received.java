package daniarachid.donation.DonationRequestManagement;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import daniarachid.donation.Adapters.HistoryRequestAdapter;
import daniarachid.donation.R;


public class history_received extends Fragment {
    RecyclerView requests;
    List<String> requestIds, itemIds, dates, donorId;
    String userId;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    HistoryRequestAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_received, container, false);
        requests = view.findViewById(R.id.receivedRecycler);

        requestIds = new ArrayList<>();
        itemIds = new ArrayList<>();
        dates =  new ArrayList<>();
        donorId = new ArrayList<>();


        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();

        retrieveRequests();


        // Inflate the layout for this fragment
        return view;
    }

    private void retrieveRequests() {
        fStore.collection("DonationRequest").whereEqualTo("receiverId", userId).whereEqualTo("requestStatus", "Donated").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    if (doc.exists()) {
                        requestIds.add(doc.getId());
                        itemIds.add(doc.get("itemId").toString());
                        dates.add(doc.get("requestDate").toString());
                        donorId.add(doc.get("donorId").toString());



                    }
                }
                adapter = new HistoryRequestAdapter(getContext(), requestIds, itemIds, dates, donorId);
                LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                requests.setLayoutManager(manager);
                requests.setAdapter(adapter);

            }
        });
    }
}