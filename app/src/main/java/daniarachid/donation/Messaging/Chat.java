package daniarachid.donation.Messaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import daniarachid.donation.Adapters.ChatAdapter;
import daniarachid.donation.Administration.AdminControl;
import daniarachid.donation.DonationManagement.MainDonation;
import daniarachid.donation.DonationManagement.TestMyItem;
import daniarachid.donation.DonationRequestManagement.DonationRequestHistory;
import daniarachid.donation.DonationRequestManagement.TestDonorRequestList;
import daniarachid.donation.DonationRequestManagement.TestReceiverRequestList;
import daniarachid.donation.MainActivity;
import daniarachid.donation.R;
import daniarachid.donation.UserAccount.UserProfile;

public class Chat extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView messages;
    static List<String> userId, userName, message, date, senderId, receiverId;
    ChatAdapter adapter;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String currentUserId, authorized;

    TextView txt;
    public DrawerLayout drawer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setNavigationDrawer();

        messages = findViewById(R.id.messagesRec);
        userId = new ArrayList<>();
        userName = new ArrayList<>();
        message = new ArrayList<>();
        date = new ArrayList<>();
        senderId = new ArrayList<>();
        receiverId = new ArrayList<>();


        txt = findViewById(R.id.txtEmptyChat);
        txt.setVisibility(View.GONE);

        getUserMessages();
    }

    private void setNavigationDrawer() {

        // set the picture here
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        currentUserId = fAuth.getCurrentUser().getUid();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.constraint_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        //authorize user
        DocumentReference document =  fStore.collection("Users").document(currentUserId);
        document.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()) {
                        authorized = doc.get("isAuthorized").toString();
                        if (authorized=="true") {
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
        DocumentReference documentReference = fStore.collection("Users").document(currentUserId);
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
                break;
            case R.id.nav_adminsControl:
                startActivity(new Intent(getApplicationContext(), AdminControl.class));
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

    public void getUserMessages() {
       fAuth = FirebaseAuth.getInstance();
       fStore = FirebaseFirestore.getInstance();
       currentUserId = fAuth.getCurrentUser().getUid();



       fStore.collection("Messages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
           @Override
           public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
               for (QueryDocumentSnapshot doc : task.getResult()) {
                   if (currentUserId.equals(doc.get("receiver").toString())) {

                      //Log.d("CheckMe", "receiver");
                       userId.add(doc.get("receiver").toString());
                       message.add(doc.get("message").toString());
                       date.add(doc.get("time").toString());

                       receiverId.add(doc.get("sender").toString());


                       }
                   else if (currentUserId.equals(doc.get("sender").toString())) {
                       userId.add(doc.get("sender").toString());
                       message.add(doc.get("message").toString());
                       date.add(doc.get("time").toString());
                       receiverId.add(doc.get("receiver").toString());

                       //Log.d("CheckMe", "sender");
                   }
                   }

               //remove duplicate data from the arraylists
               Set<String> set = new HashSet<>(userId);
               Set<String> messageSet = new HashSet<>(message);
               Set<String> dateSet = new HashSet<>(date);
               Set<String> receiverSet = new HashSet<>(receiverId);
               userId.clear();
               message.clear();
               date.clear();
               receiverId.clear();
               userId.addAll(set);
               message.addAll(messageSet);
               date.addAll(dateSet);
               receiverId.addAll(receiverSet);
               //filter the arraylists




               // set the adapter
               adapter = new ChatAdapter(getApplicationContext(), userId, message, date, receiverId);

               LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
               messages.setLayoutManager(llm);
               messages.setAdapter(adapter);

               if (userId.size() ==0) {
                   TextView text = findViewById(R.id.txtEmptyChat);
                   text.setVisibility(View.VISIBLE);
                   text.setText("there are no conversations");
               }
           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull @NotNull Exception e) {
               //do something
           }
       });





    }



    public void signout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();;

    }



}