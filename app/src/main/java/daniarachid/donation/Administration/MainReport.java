package daniarachid.donation.Administration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.Call;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import daniarachid.donation.DonationManagement.MainDonation;
import daniarachid.donation.DonationManagement.TestMyItem;
import daniarachid.donation.DonationRequestManagement.DonationRequestHistory;
import daniarachid.donation.DonationRequestManagement.TestDonorRequestList;
import daniarachid.donation.DonationRequestManagement.TestReceiverRequestList;
import daniarachid.donation.Messaging.Chat;
import daniarachid.donation.R;
import daniarachid.donation.UserAccount.UserProfile;

public class MainReport extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Spinner spinner;
    TextView dateFrom, dateTo;
    DatePickerDialog dateFromDialog, dateToDialog;
    Button btnGenerate;
    Fragment_donated_items_by_category fragment_donated_items_by_category;
    Fragment_donated_items_by_donor donated_items_by_donor;
    Fragment_donated_items_by_receiver donated_items_by_receiver;
    Fragment_active_items_by_category fragment_active_items_by_category;
    int reportType;

    String strDateFrom, strDateTo;

    public DrawerLayout drawer;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_report);


        spinner = findViewById(R.id.spinner);
        dateFrom = findViewById(R.id.dateFrom);
        dateTo = findViewById(R.id.dateTo);
        btnGenerate = findViewById(R.id.btnGenerateReport);

        initDatePicker();

        dateFrom.setText(getTodayDate());
        dateTo.setText(getTodayDate());

        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateFromDialog.show();
            }
        });

        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dateToDialog.
                dateToDialog.show();
            }
        });


        fragment_donated_items_by_category = new Fragment_donated_items_by_category();
        donated_items_by_donor = new Fragment_donated_items_by_donor();
        donated_items_by_receiver = new Fragment_donated_items_by_receiver();
        fragment_active_items_by_category = new Fragment_active_items_by_category();


        List<String> reportTypes = new ArrayList<>();
        reportTypes.add(0, "Select report type..");
        reportTypes.add("Donated items by category");
        reportTypes.add("Donated items by donors");
        reportTypes.add("Donated items by receivers");
        reportTypes.add("Active donation items by category");


        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, reportTypes);
        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);


        setNavigationDrawer();
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("CheckMe", "button clicked");
                strDateFrom = dateFrom.getText().toString();
                strDateTo = dateTo.getText().toString();
                //check spinner
                if (spinner.getSelectedItemPosition() < 1) {
                    Toast.makeText(getApplicationContext(), "Please select a report type", Toast.LENGTH_SHORT).show();
                    return;
                }


                String item = spinner.getSelectedItem().toString();
                // tvFilter.setVisibility(View.VISIBLE);

                switch (item) {
                    case "Donated items by category":
                        setFragment(fragment_donated_items_by_category);
                        reportType = 1;
                        break;
                    case "Donated items by donors":
                        setFragment(donated_items_by_donor);
                        reportType = 2;
                        break;
                    case "Donated items by receivers":
                        setFragment(donated_items_by_receiver);
                        reportType = 3;
                        break;
                    case "Active donation items by category":
                        setFragment(fragment_active_items_by_category);


                        break;


                    default:
                        //nothing
                }


            }
        });


    }

    private String getTodayDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        month = month + 1;
        return makeDateString(day, month, year);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);


                if (view == dateFromDialog.getDatePicker()) {

                    dateFrom.setText(date);
                } else if (view == dateToDialog.getDatePicker()) {
                    //get today date

                    dateTo.setText(date);


                }
            }
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;


        dateFromDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);

        dateToDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        dateToDialog.getDatePicker().setMaxDate(new Date().getTime());


    }

    private String makeDateString(int day, int month, int year) {
        return day + "-" + getMonthFormat(month) + "-" + year;
    }

    private String getMonthFormat(int month) {
        String strMonth = "";
        switch (month) {
            case 1:
                strMonth = "01";
                break;
            case 2:
                strMonth = "02";
                break;
            case 3:
                strMonth = "03";
                break;
            case 4:
                strMonth = "04";
                break;
            case 5:
                strMonth = "05";
                break;
            case 6:
                strMonth = "06";
                break;
            case 7:
                strMonth = "07";
                break;
            case 8:
                strMonth = "08";
                break;
            case 9:
                strMonth = "09";
                break;
            case 10:
                strMonth = "10";
                break;
            case 11:
                strMonth = "11";
                break;
            case 12:
                strMonth = "12";
                break;
            default:
                strMonth = "01";


        }
        return strMonth;
    }


    public void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();


        Fragment current = getSupportFragmentManager().findFragmentById(R.id.main_frame);
        if (current != null) {

            Bundle bundle = new Bundle();
            bundle.putString("dateFrom", strDateFrom);
            bundle.putString("dateTo", strDateTo);


            int currentId = current.getId();

            if (currentId == fragment_active_items_by_category.getId()) {
                fragment = new Fragment_active_items_by_category();
            }

            else if (currentId == fragment_donated_items_by_category.getId()) {
                fragment = new Fragment_donated_items_by_category();
            }

            else if (currentId == donated_items_by_donor.getId()) {
                fragment = new Fragment_donated_items_by_donor();
            }

            else if (currentId == donated_items_by_receiver.getId()) {
                fragment = new Fragment_donated_items_by_receiver();
            }

            fragment.setArguments(bundle);
            fragmentTransaction.replace(current.getId(), fragment).commit();
        }

        else {
            Bundle bundle = new Bundle();
            bundle.putString("dateFrom", strDateFrom);
            bundle.putString("dateTo", strDateTo);
            fragment.setArguments(bundle);

            fragmentTransaction.replace(R.id.main_frame, fragment);
            fragmentTransaction.commit();
        }


        }

    private void setNavigationDrawer() {
        //navigation drawer setting

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = findViewById(R.id.drawer);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        ImageView nav_profileImg = headerView.findViewById(R.id.nav_user_image);
        // set the picture here
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
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


    public  void signout(){}




    //option menu


    private void download() {
    }


}