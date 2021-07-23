package daniarachid.donation.Administration;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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
import java.util.List;

import daniarachid.donation.Adapters.ReportAdapter;
import daniarachid.donation.R;


public class Fragment_active_items_by_category extends Fragment {


    FirebaseFirestore fStore;
    List<String> categories, itemIds, donatedItemsIds;
    List<Integer> donatedItems;
    ReportAdapter adapter;
    RecyclerView reports;



    String dateFrom, dateTo;
    Date dateStart, dateEnd, actualDate;


    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateFrom = getArguments().getString("dateFrom");
        dateTo = getArguments().getString("dateTo");
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_active_items_by_category, container, false);
        categories = new ArrayList<>();
        donatedItems = new ArrayList<>();
        donatedItemsIds = new ArrayList<>();
        itemIds = new ArrayList<>();
        reports = view.findViewById(R.id.itemsByCategory);

        Log.d("CheckMe","On create view is executed");


        categories.add("Appliances");
        categories.add("Food");
        categories.add("Kids Clothes");
        categories.add("Men Clothes");
        categories.add("Toys");
        categories.add("Women Clothes");



        try {
            //convert date strings to actual date
            dateStart = new SimpleDateFormat("dd-MM-yyyy").parse(dateFrom);
            dateEnd = new SimpleDateFormat("dd-MM-yyyy").parse(dateTo);
            Log.d("CheckMe", "start --> " + dateFrom + " " +
                    "end --> " + dateTo);
        }
        catch (ParseException e) {
            Log.d("CheckMe", e.getMessage());
        }


        getAscData();

        return view;
    }



    private void getAscData() {


        fStore = FirebaseFirestore.getInstance();
        // get the donated items
        fStore.collection("DonationRequest").whereEqualTo("requestStatus", "Donated")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {

                        for (QueryDocumentSnapshot doc : task.getResult())
                        {
                            donatedItemsIds.add(doc.get("itemId").toString());

                        }




                        fStore.collection("Items").get().addOnCompleteListener(
                                new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task)
                                    {
                                        if (task.isSuccessful())
                                        {

                                            if (!task.getResult().isEmpty())
                                            {
                                                int food = 0, women = 0, men = 0, kids = 0, toys = 0, appliances = 0;
                                                for (QueryDocumentSnapshot document : task.getResult())
                                                {
                                                    String date = document.get("postedDate").toString();
                                                    try {
                                                        actualDate = new SimpleDateFormat("dd-MM-yyyy").parse(date);
                                                    }
                                                    catch (ParseException e) {
                                                        Log.d("CheckMe", e.getMessage());
                                                    }

                                                    if ((actualDate.equals(dateStart) || actualDate.after(dateStart))&&
                                                            (actualDate.equals(dateEnd) || actualDate.before(dateEnd))) {
                                                        if (!donatedItemsIds.contains(document.getId())) {
                                                            if (!itemIds.contains(document.getId())) {
                                                                itemIds.add(document.getId());
                                                                switch (document.get("category").toString())
                                                                {
                                                                    case "Food" :
                                                                        food ++;
                                                                        break;
                                                                    case "Women Clothes" :
                                                                        women ++;
                                                                        break;
                                                                    case "Men Clothes" :
                                                                        men ++;
                                                                        break;
                                                                    case "Kids Clothes" :
                                                                        kids ++;
                                                                        break;
                                                                    case "Toys" :
                                                                        toys ++;
                                                                        break;
                                                                    case "Appliances" :
                                                                        appliances ++;
                                                                        break;
                                                                    default:

                                                                }
                                                            }
                                                        }
                                                    }


                                                }


                                                //set the adapter here
                                                List<Integer> number = new ArrayList<>();
                                                number.add(food);
                                                number.add(women);
                                                number.add(men);
                                                number.add(kids);
                                                number.add(toys);
                                                number.add(appliances);
                                                adapter = new ReportAdapter(getContext(), itemIds, categories, number, 4);
                                                LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                                                reports.setLayoutManager(manager);
                                                reports.setAdapter(adapter);
                                            }
                                        }
                                    }
                                }
                        );

                    }
                }
            }
        });



    }




    private void getDescData() {
        reports.removeAllViews();
        //get the Donated itemIds
        //reports.removeAllViews();
        fStore = FirebaseFirestore.getInstance();
        fStore.collection("Items").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

                if (task.isSuccessful())
                {
                    if (!task.getResult().isEmpty())
                    {
                        int food = 0, women = 0, men = 0, kids = 0, toys = 0, appliances = 0;
                        for (QueryDocumentSnapshot doc : task.getResult())
                        {






                                    //how to handle the category
                                    switch (doc.get("category").toString())
                                    {

                                        case "Food" :
                                            food ++;
                                            break;
                                        case "Women Clothes" :
                                            women ++;
                                            break;
                                        case "Men Clothes" :
                                            men ++;
                                            break;
                                        case "Kids Clothes" :
                                            kids ++;
                                            break;
                                        case "Toys" :
                                            toys ++;
                                            break;
                                        case "Appliances" :
                                            appliances ++;
                                            break;

                                    }






                        }
                        List<Integer> number = new ArrayList<>();
                        number.add(food);
                        number.add(women);
                        number.add(men);
                        number.add(kids);
                        number.add(toys);
                        number.add(appliances);

                        //sort the data (Descending)

                        for (int i =0 ; i < number.size(); i++)
                        {
                            for (int j = i + 1;  j< number.size(); j ++)
                            {
                                if (number.get(i) > number.get(j)) {
                                    int temp = 0;
                                    temp = number.get(i);
                                    number.set(i, number.get(j));
                                    number.set(j, temp);

                                    String tempo = categories.get(i);
                                    categories.set(i, categories.get(j));
                                    categories.set(j, tempo);


                                }
                            }

                        }

                        //set the adapter now
                        adapter = new ReportAdapter(getContext(), itemIds, categories, number,4);
                        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                        reports.setLayoutManager(manager);
                        reports.setAdapter(adapter);

                    }


                }
            }
        });


    }
}