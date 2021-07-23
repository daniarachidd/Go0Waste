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

import org.jetbrains.annotations.NotNull;

import java.io.File;
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


public class Fragment_donated_items_by_category extends Fragment {

    List<String> categories, itemIds;
    List<Integer> donatedItems;
    ReportAdapter adapter;
    RecyclerView reports;
    FirebaseFirestore fStore;
    Spinner spinner;
    String dateFrom, dateTo;
    Date dateStart, dateEnd, actualDate;
   // TextView download;
   // Bitmap bmp, scaledBmp;

   // Date date;
    //DateFormat dateFormat;


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


        spinner = view.findViewById(R.id.spinnerFilter);
       // download = view.findViewById(R.id.txtDownload);
        List<String> filters = new ArrayList<>();
        filters.add("High to low");
        filters.add("Low to high");

       // bmp = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
       // scaledBmp = Bitmap.createScaledBitmap(bmp, 1200, 518, false);



        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, filters);
        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        //spinner.setSelected(false);




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



        getData();


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


        /*
        download.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                createPdf(categories, donatedItems);
            }
        });


         */
        // Inflate the layout for this fragment
        return view;
    }

    /*

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createPdf(List<String> categories, List<Integer> donatedItems) {

        date = new Date();

        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint titlePaint = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        canvas.drawBitmap(scaledBmp, 0, 0,paint);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        titlePaint.setTextSize(70);
        canvas.drawText("Report of donated items", 600, 500, titlePaint);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        titlePaint.setTextSize(50);
        canvas.drawText("From: " + dateFrom + " To " + dateTo, 600, 550, titlePaint);

        dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        titlePaint.setTextAlign(Paint.Align.LEFT);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        titlePaint.setTextSize(35f);
        canvas.drawText("Date: " + dateFormat.format(date), 20, 640, titlePaint);


        //draw the table
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawRect(20, 780, 1200-20, 860, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText("No. ", 40, 830, paint);
        canvas.drawText("Category ", 400, 830, paint);
        canvas.drawText("Donated Items", 900, 830, paint);
        canvas.drawLine(180, 790, 180,  840, paint);
        canvas.drawLine(680, 790, 680, 840, paint);


        //fill data
        canvas.drawText("1", 40, 950, paint);
        canvas.drawText("Food", 420, 950, paint);
        canvas.drawText("200", 920, 950, paint);

        canvas.drawText("2", 40, 1050, paint);
        canvas.drawText("Somehting", 420, 1050, paint);
        canvas.drawText("40", 920, 1050, paint);

        canvas.drawText("3", 40, 1150, paint);
        canvas.drawText("Anything", 420, 1150, paint);
        canvas.drawText("20", 920, 1150, paint);



        pdfDocument.finishPage(page);

        File file = new File(pdfPath, "report.pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        pdfDocument.close();
    }



     */
    private void getDescData() {
        reports.removeAllViews();
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

                        //set the adapter now
                        adapter = new ReportAdapter(getContext(), itemIds, categories,1);
                        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                        reports.setLayoutManager(manager);
                        reports.setAdapter(adapter);

                    }


                }
            }
        });
    }
    private void getAscData() {

        reports.removeAllViews();
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

                        //set the adapter now
                        adapter = new ReportAdapter(getContext(), itemIds, categories,1);
                        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                        reports.setLayoutManager(manager);
                        reports.setAdapter(adapter);

                    }


                }
            }
        });


    }

    public void getData()   {


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


                       // Log.d("CheckMe", dateEnd.toString());
                        if ((actualDate.equals(dateStart) || actualDate.after(dateStart))&&
                                (actualDate.equals(dateEnd) || actualDate.before(dateEnd))) {
                            //Log.d("CheckMe", " actual datebigger");
                            itemIds.add(doc.get("itemId").toString());

                        }


                    }




                        adapter = new ReportAdapter(getContext(), itemIds, categories,1);
                        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                        reports.setLayoutManager(manager);
                        reports.setAdapter(adapter);



                }
            }
        });




    }


}