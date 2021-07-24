package daniarachid.donation.Administration;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
//import com.itextpdf.kernel.pdf.PdfDocument;


import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import daniarachid.donation.Adapters.ReportAdapter;
import daniarachid.donation.R;
;


public class Fragment_donated_items_by_category extends Fragment {

    List<String> categories, itemIds;
    List<Integer> donatedItems;
    ReportAdapter adapter;
    RecyclerView reports;
    FirebaseFirestore fStore;
    Spinner spinner;
    String dateFrom, dateTo;
    Date dateStart, dateEnd, actualDate;
    TextView download;
    Bitmap bmp, scaledBmp;

    Date date;
    DateFormat dateFormat;
    List<Integer> number;


    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dateFrom = getArguments().getString("dateFrom");
        dateTo = getArguments().getString("dateTo");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donated_items_by_category, container, false);

        number = new ArrayList<>();

        spinner = view.findViewById(R.id.spinnerFilter);
        download = view.findViewById(R.id.txtDownload);
        List<String> filters = new ArrayList<>();
        filters.add("High to low");
        filters.add("Low to high");

        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        scaledBmp = Bitmap.createScaledBitmap(bmp, 1200, 300, false);



        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, filters);
        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);




        categories = new ArrayList<>();
        donatedItems = new ArrayList<>();
        itemIds = new ArrayList<>();
        reports = view.findViewById(R.id.byDonorSpinner);



        fStore = FirebaseFirestore.getInstance();


        categories.add("Food");
        categories.add("Women Clothes");
        categories.add("Men Clothes");
        categories.add("Kids Clothes");
        categories.add("Toys");
        categories.add("Appliances");


        try {
            //convert date strings to actual date
            dateStart = new SimpleDateFormat("dd-MM-yyyy").parse(dateFrom);
            dateEnd = new SimpleDateFormat("dd-MM-yyyy").parse(dateTo);
        }
        catch (ParseException e) {
            Log.d("CheckMe", e.getMessage());
        }



        //getData/();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                {
                    getDescData();
                }
                else if (position == 1)
                {
                    getAscData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        download.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                createPdf();
            }
        });



        // Inflate the layout for this fragment
        return view;
    }





    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createPdf() {
        date = new Date();


        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1200,2010,1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        //logo
        canvas.drawBitmap(scaledBmp, 0, 0,paint);

        //report title
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        paint.setTextSize(45);
        canvas.drawText("Report of donated items", 600, 350, paint);

        //report date range
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        paint.setTextSize(35);
        canvas.drawText("From: " + dateFrom + " To " + dateTo, 600, 400, paint);


        //report date generated
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(30);
        canvas.drawText("Date: " + dateFormat.format(date), 20, 480, paint);



        //table
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawRect(100, 600, 1200-100, 1050, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText("No. ", 120, 650, paint);
        canvas.drawText("Category ", 480, 650, paint);
        canvas.drawText("Donated Items", 820, 650, paint);
        canvas.drawLine(240, 610, 240,  660, paint);
        canvas.drawLine(800, 610, 800, 660, paint);


        //get the date to fill the table
        if (spinner.getSelectedItemPosition() == 0) {

            //desc
            fStore = FirebaseFirestore.getInstance();
            //get the itemId from DonationRequest
            fStore.collection("DonationRequest")
                    .whereEqualTo("requestStatus", "Donated")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot doc : task.getResult()) {

                            String date = doc.get("requestDate").toString();
                            try {
                                actualDate = new SimpleDateFormat("dd-MM-yyyy").parse(date);
                            }
                            catch (ParseException e) {
                                Log.d("CheckMe", e.getMessage());
                            }



                            if ((actualDate.equals(dateStart) || actualDate.after(dateStart))&&
                                    (actualDate.equals(dateEnd) || actualDate.before(dateEnd))) {

                                if (!itemIds.contains(doc.get("itemId"))) {
                                    itemIds.add(doc.get("itemId").toString());
                                }


                            }


                        }


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

                                            for (int i = 0 ; i < itemIds.size(); i++)
                                            {

                                                if(doc.getId().equals(itemIds.get(i)))
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
                                                if (number.get(i) < number.get(j)) {
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


                                        //fill data
                                        //fill the data here
                                        int xNo = 120;
                                        int xCategory = 480;
                                        int xDonatedItems = 900;
                                        int y = 720;
                                        for (int i = 0; i< categories.size(); i++) {
                                            String no = String.valueOf(i+1);
                                            canvas.drawText(no, xNo, y, paint);
                                            canvas.drawText(categories.get(i), xCategory, y, paint);
                                            canvas.drawText(String.valueOf(number.get(i)), xDonatedItems, y, paint);
                                            y = y +60;
                                        }

                                        //create the file here
                                        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                        pdfDocument.finishPage(page);
                                        String fileName = "Report_of_donated_items(" + dateFormat.format(date) + ").pdf";
                                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

                                        try {

                                            pdfDocument.writeTo(new FileOutputStream(file));
                                            Toast.makeText(getContext(), "Report has been downloaded", Toast.LENGTH_SHORT).show();

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            Log.d("CheckMe", e.getMessage());
                                        }


                                        pdfDocument.close();



                                    }

                                    else {


                                        number.add(0);
                                        number.add(0);
                                        number.add(0);
                                        number.add(0);
                                        number.add(0);
                                        number.add(0);

                                        //fill the data here
                                        int xNo = 120;
                                        int xCategory = 480;
                                        int xDonatedItems = 900;
                                        int y = 720;
                                        for (int i = 0; i< categories.size(); i++) {
                                            String no = String.valueOf(i+1);
                                            canvas.drawText(no, xNo, y, paint);
                                            canvas.drawText(categories.get(i), xCategory, y, paint);
                                            canvas.drawText(String.valueOf(number.get(i)), xDonatedItems, y, paint);
                                            y = y +60;
                                        }

                                        //create the file here
                                        pdfDocument.finishPage(page);
                                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/Hello.pdf");

                                        try {
                                            pdfDocument.writeTo(new FileOutputStream(file));
                                            Toast.makeText(getContext(), "Report has been downloaded", Toast.LENGTH_SHORT).show();

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            Log.d("CheckMe", e.getMessage());
                                        }


                                        pdfDocument.close();
                                    }



                                }
                            }
                        });

                    }
                }
            });





        } else if (spinner.getSelectedItemPosition() == 1) {

            //asc
            fStore = FirebaseFirestore.getInstance();
            //get the itemId from DonationRequest
            fStore.collection("DonationRequest")
                    .whereEqualTo("requestStatus", "Donated")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot doc : task.getResult()) {

                            String date = doc.get("requestDate").toString();
                            try {
                                actualDate = new SimpleDateFormat("dd-MM-yyyy").parse(date);
                            }
                            catch (ParseException e) {
                                Log.d("CheckMe", e.getMessage());
                            }



                            if ((actualDate.equals(dateStart) || actualDate.after(dateStart))&&
                                    (actualDate.equals(dateEnd) || actualDate.before(dateEnd))) {

                                if (!itemIds.contains(doc.get("itemId"))) {
                                    itemIds.add(doc.get("itemId").toString());
                                }


                            }


                        }


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

                                            for (int i = 0 ; i < itemIds.size(); i++)
                                            {

                                                if(doc.getId().equals(itemIds.get(i)))
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


                                        //fill data
                                        //fill the data here
                                        int xNo = 120;
                                        int xCategory = 480;
                                        int xDonatedItems = 900;
                                        int y = 720;
                                        for (int i = 0; i< categories.size(); i++) {
                                            String no = String.valueOf(i+1);
                                            canvas.drawText(no, xNo, y, paint);
                                            canvas.drawText(categories.get(i), xCategory, y, paint);
                                            canvas.drawText(String.valueOf(number.get(i)), xDonatedItems, y, paint);
                                            y = y +60;
                                        }

                                        //create the file here
                                        pdfDocument.finishPage(page);
                                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/Hello.pdf");

                                        try {
                                            pdfDocument.writeTo(new FileOutputStream(file));
                                            Toast.makeText(getContext(), "Report has been downloaded", Toast.LENGTH_SHORT).show();

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            Log.d("CheckMe", e.getMessage());
                                        }


                                        pdfDocument.close();



                                    }

                                    else {


                                        number.add(0);
                                        number.add(0);
                                        number.add(0);
                                        number.add(0);
                                        number.add(0);
                                        number.add(0);

                                        //fill the data here
                                        int xNo = 120;
                                        int xCategory = 480;
                                        int xDonatedItems = 900;
                                        int y = 720;
                                        for (int i = 0; i< categories.size(); i++) {
                                            String no = String.valueOf(i+1);
                                            canvas.drawText(no, xNo, y, paint);
                                            canvas.drawText(categories.get(i), xCategory, y, paint);
                                            canvas.drawText(String.valueOf(number.get(i)), xDonatedItems, y, paint);
                                            y = y +60;
                                        }

                                        //create the file here
                                        pdfDocument.finishPage(page);
                                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/Hello.pdf");

                                        try {
                                            pdfDocument.writeTo(new FileOutputStream(file));
                                            Toast.makeText(getContext(), "Report has been downloaded", Toast.LENGTH_SHORT).show();

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            Log.d("CheckMe", e.getMessage());
                                        }


                                        pdfDocument.close();
                                    }



                                }
                            }
                        });

                    }
                }
            });



        }



    }



    private void getDescData() {

        reports.removeAllViews();
        fStore = FirebaseFirestore.getInstance();

        //get item ids
        fStore.collection("DonationRequest")
                .whereEqualTo("requestStatus", "Donated")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot doc : task.getResult()) {

                        String date = doc.get("requestDate").toString();
                        try {
                            actualDate = new SimpleDateFormat("dd-MM-yyyy").parse(date);
                        }
                        catch (ParseException e) {
                            Log.d("CheckMe", e.getMessage());
                        }



                        if ((actualDate.equals(dateStart) || actualDate.after(dateStart))&&
                                (actualDate.equals(dateEnd) || actualDate.before(dateEnd))) {

                            if(!itemIds.contains(doc.get("itemId"))) {
                                itemIds.add(doc.get("itemId").toString());
                            }


                        }


                    }



                    //get donation requests
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

                                        for (int i = 0 ; i < itemIds.size(); i++)
                                        {

                                            if(doc.getId().equals(itemIds.get(i)))
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
                                            if (number.get(i) < number.get(j)) {
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


                                    //set adapter here
                                    adapter = new ReportAdapter(getContext(), itemIds, categories,number,1);
                                    LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                                    reports.setLayoutManager(manager);
                                    reports.setAdapter(adapter);



                                }

                                else {


                                    number.add(0);
                                    number.add(0);
                                    number.add(0);
                                    number.add(0);
                                    number.add(0);
                                    number.add(0);

                                    //set another adapter here
                                    adapter = new ReportAdapter(getContext(), itemIds, categories,number,1);
                                    LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                                    reports.setLayoutManager(manager);
                                    reports.setAdapter(adapter);
                                }



                            }
                        }
                    });

                }
            }
        });

    }
    private void getAscData() {


        reports.removeAllViews();
        fStore = FirebaseFirestore.getInstance();
        //get item ids
        fStore.collection("DonationRequest")
                .whereEqualTo("requestStatus", "Donated")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot doc : task.getResult()) {

                        String date = doc.get("requestDate").toString();
                        try {
                            actualDate = new SimpleDateFormat("dd-MM-yyyy").parse(date);
                        }
                        catch (ParseException e) {
                            Log.d("CheckMe", e.getMessage());
                        }



                        if ((actualDate.equals(dateStart) || actualDate.after(dateStart))&&
                                (actualDate.equals(dateEnd) || actualDate.before(dateEnd))) {

                            if(!itemIds.contains(doc.get("itemId"))) {
                                itemIds.add(doc.get("itemId").toString());
                            }


                        }


                    }



                    //get donation requests
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

                                        for (int i = 0 ; i < itemIds.size(); i++)
                                        {

                                            if(doc.getId().equals(itemIds.get(i)))
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


                                    //set adapter here
                                    adapter = new ReportAdapter(getContext(), itemIds, categories,number,1);
                                    LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                                    reports.setLayoutManager(manager);
                                    reports.setAdapter(adapter);



                                }

                                else {


                                    number.add(0);
                                    number.add(0);
                                    number.add(0);
                                    number.add(0);
                                    number.add(0);
                                    number.add(0);

                                    //set another adapter here
                                    adapter = new ReportAdapter(getContext(), itemIds, categories,number,1);
                                    LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                                    reports.setLayoutManager(manager);
                                    reports.setAdapter(adapter);
                                }



                            }
                        }
                    });

                }
            }
        });


    }




}