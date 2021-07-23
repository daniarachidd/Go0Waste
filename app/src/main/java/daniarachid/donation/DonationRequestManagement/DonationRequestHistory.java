package daniarachid.donation.DonationRequestManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import daniarachid.donation.Adapters.DonationHistoryPagerAdapter;
import daniarachid.donation.Administration.AdminControl;
import daniarachid.donation.Administration.MainReport;
import daniarachid.donation.DonationManagement.MainDonation;
import daniarachid.donation.DonationManagement.TestMyItem;
import daniarachid.donation.MainActivity;
import daniarachid.donation.Messaging.Chat;
import daniarachid.donation.R;
import daniarachid.donation.UserAccount.UserProfile;

public class DonationRequestHistory extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    ViewPager viewPager;
    TabLayout tabLayout;
    TabItem donated, received;
    DonationHistoryPagerAdapter adapter;
    String authorized, userId;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_request_history);

        setTablayout();
        setNavigationDrawer();



        adapter = new DonationHistoryPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

    }

    private void setTablayout() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tablayout);
        donated = findViewById(R.id.tabDonated);
        received = findViewById(R.id.tabReceived);

    }

    private void setNavigationDrawer() {

        // set the picture here
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        //authorize user
        DocumentReference document =  fStore.collection("Users").document(userId);
        document.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()) {
                        authorized = doc.get("isAuthorized").toString();
                        if (authorized=="true") {
                            //Log.d("CheckMe", "authorized");
                            navigationView.getMenu().clear();
                            navigationView.inflateMenu(R.menu.nav_menu_admin);

                        }
                    }
                }
            }
        });



        ImageView nav_profileImg = headerView.findViewById(R.id.nav_user_image);

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


        //display name and email
        TextView mUsername = headerView.findViewById(R.id.username);
        TextView mEmail = headerView.findViewById(R.id.useremail);

        //String username, email;
        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()) {
                    mUsername.setText(doc.get("name").toString());
                    mEmail.setText(doc.get("email").toString());
                }
            }
        });

        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        //viewPager.setAdapter(adapter);

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


    public  void signout(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();;
    }
}