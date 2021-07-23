package daniarachid.donation.Administration;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import daniarachid.donation.Adapters.ReportAdapter;
import daniarachid.donation.R;


public class Fragment_donated_items_by_receiver extends Fragment {

    List<String> itemIds, receiverIds;
    FirebaseFirestore fStore;
    ReportAdapter adapter;
    RecyclerView reports;
    String dateFrom, dateTo;
    Date dateStart, dateEnd, actualDate;
    TextView mNo, mReceiver, mReceived;


    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateFrom = getArguments().getString("dateFrom");
        dateTo = getArguments().getString("dateTo");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donated_items_by_receiver, container, false);

        mNo = view.findViewById(R.id.txtNo);
        mReceiver = view.findViewById(R.id.txtReceiver);
        mReceived = view.findViewById(R.id.txtReceived);
        itemIds = new ArrayList<>();
        receiverIds = new ArrayList<>();
        fStore = FirebaseFirestore.getInstance();
        reports = view.findViewById(R.id.byReceiverSpinner);

        try {
            //convert date strings to actual date
            dateStart = new SimpleDateFormat("dd-MM-yyyy").parse(dateFrom);
            dateEnd = new SimpleDateFormat("dd-MM-yyyy").parse(dateTo);
        }
        catch (ParseException e) {
            Log.d("CheckMe", e.getMessage());
        }


        getData();

        return view;
    }

    private void getData() {
        fStore.collection("DonationRequest").whereEqualTo("requestStatus", "Donated")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        if (!task.getResult().isEmpty()) {
                            String date = doc.get("requestDate").toString();
                            try {
                                actualDate = new SimpleDateFormat("dd-MM-yyyy").parse(date);
                            }
                            catch (ParseException e) {
                                Log.d("CheckMe", e.getMessage());
                            }

                            if ((actualDate.equals(dateStart) || actualDate.after(dateStart))&&
                                    (actualDate.equals(dateEnd) || actualDate.before(dateEnd))) {

                                itemIds.add(doc.get("itemId").toString());
                                receiverIds.add(doc.get("receiverId").toString());

                            }




                        }
                    }
                }

                Set<String> distinctDonorIds = new HashSet<>(receiverIds);
                receiverIds.clear();
                receiverIds.addAll(distinctDonorIds);

                if (receiverIds.size() == 0 ) {

                    mNo.setText("No data found");

                    mReceived.setText("");
                    mReceiver.setText("");
                }
                adapter = new ReportAdapter(getContext(), itemIds, receiverIds, 3);
                LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                reports.setLayoutManager(manager);
                reports.setAdapter(adapter);
            }
        });
    }
}