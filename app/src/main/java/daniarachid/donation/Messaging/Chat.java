package daniarachid.donation.Messaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import daniarachid.donation.Adapters.ChatAdapter;
import daniarachid.donation.DonationRequestManagement.DonorDonationRequest;
import daniarachid.donation.DonationRequestManagement.TestReceiverRequestList;
import daniarachid.donation.Entity.MessageModel;
import daniarachid.donation.MainActivity;
import daniarachid.donation.R;
import daniarachid.donation.UserAccount.UserProfile;

public class Chat extends AppCompatActivity {

    RecyclerView messages;
    static List<String> userId, userName, message, date, senderId, receiverId;
    ChatAdapter adapter;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String currentUserId;
    MessageViewModel viewModel;
    TextView txt;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

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
           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull @NotNull Exception e) {
               //do something
           }
       });





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signout: signout();
                break;

            case R.id.userProfile:
                startActivity(new Intent(getApplicationContext(), UserProfile.class));
                break;
            case R.id.donationRequestsRec:
                startActivity(new Intent(getApplicationContext(), TestReceiverRequestList.class));
                break;
            case R.id.receivedDonationRequests:
                startActivity(new Intent(getApplicationContext(), DonorDonationRequest.class));
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
        finish();;

    }



}