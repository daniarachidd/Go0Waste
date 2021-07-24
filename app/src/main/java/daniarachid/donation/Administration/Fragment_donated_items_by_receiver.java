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
import java.text.DateFormat;
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

    List<String> itemIds, receiverIds, receiverNames;
    List<Integer> number;
    FirebaseFirestore fStore;
    ReportAdapter adapter;
    RecyclerView reports;
    String dateFrom, dateTo;
    Date dateStart, dateEnd, actualDate, date;
    TextView mNo, mReceiver, mReceived, download;
    Bitmap bmp, scaledBmp;
    DateFormat dateFormat;
    int numberOfItems = 0;


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
        number = new ArrayList<>();
        receiverNames =  new ArrayList<>();
        fStore = FirebaseFirestore.getInstance();
        reports = view.findViewById(R.id.byReceiverSpinner);
        download = view.findViewById(R.id.txtDownload);

        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        scaledBmp = Bitmap.createScaledBitmap(bmp, 1200, 300, false);
        try {
            //convert date strings to actual date
            dateStart = new SimpleDateFormat("dd-MM-yyyy").parse(dateFrom);
            dateEnd = new SimpleDateFormat("dd-MM-yyyy").parse(dateTo);
        }
        catch (ParseException e) {
            Log.d("CheckMe", e.getMessage());
        }


        getData();

        download.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                createPdf();
            }
        });
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createPdf() {
        date = new Date();
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        //logo
        canvas.drawBitmap(scaledBmp, 0, 0, paint);

        //report title
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        paint.setTextSize(45);
        canvas.drawText("Report of donations based on receivers", 600, 350, paint);

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
        canvas.drawRect(100, 600, 1200 - 100, 1050, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText("No. ", 120, 650, paint);
        canvas.drawText("Receiver ", 480, 650, paint);
        canvas.drawText("Items", 820, 650, paint);
        canvas.drawLine(240, 610, 240, 660, paint);
        canvas.drawLine(800, 610, 800, 660, paint);


        //get the data
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

                Set<String> distinctReceiverIds = new HashSet<>(receiverIds);
                receiverIds.clear();
                receiverIds.addAll(distinctReceiverIds);




                //get the number of items
                fStore.collection("DonationRequest").whereEqualTo("requestStatus", "Donated").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {


                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    //Log.d("CheckMe", doc.get("donorId").toString());
                                    if (doc.exists()) {

                                        for (int i =0; i < receiverIds.size(); i++) {

                                            for (int j = 0; j < receiverIds.size(); j++) {

                                                if (doc.get("receiverId").equals(receiverIds.get(j))) {

                                                    numberOfItems++;


                                                }
                                            }
                                            number.add(i, numberOfItems);


                                        }


                                    }
                                }
                                numberOfItems = 0;
                                //end of query
                                //continue here

                                //get the names
                                fStore.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (!task.getResult().isEmpty()) {

                                                for (QueryDocumentSnapshot document : task.getResult()) {

                                                    if (document.exists()) {
                                                        for (int i =0; i < receiverIds.size(); i++) {
                                                            for (int j =0; j<receiverIds.size(); j++) {
                                                                if (document.getId().equals(receiverIds.get(j))) {
                                                                    receiverNames.add(document.get("name").toString());

                                                                }
                                                            }
                                                        }


                                                    }
                                                }
                                                //end file here
                                                //fill the data here
                                                int xNo = 120;
                                                int xCategory = 480;
                                                int xDonatedItems = 900;
                                                int y = 720;
                                                for (int i = 0; i < receiverIds.size(); i++) {
                                                    String no = String.valueOf(i + 1);
                                                    canvas.drawText(no, xNo, y, paint);
                                                    canvas.drawText(receiverNames.get(i), xCategory, y, paint);
                                                    canvas.drawText(number.get(i).toString(), xDonatedItems, y, paint);
                                                    y = y + 60;
                                                }

                                                //create the file here
                                                dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                                pdfDocument.finishPage(page);
                                                String fileName = "/Report_of_donations_by_receivers(" +dateFormat.format(date) + ").pdf";
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
                                        }
                                    }
                                });

                            }
                        }
                    }
                });









            }
        });
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

                    download.setVisibility(View.GONE);
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