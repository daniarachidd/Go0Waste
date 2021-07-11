package daniarachid.donation.Messaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
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
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daniarachid.donation.Adapters.MessageAdapter;
import daniarachid.donation.DonationRequestManagement.TestReceiverRequestList;
import daniarachid.donation.Entity.MessageModel;
import daniarachid.donation.MainActivity;
import daniarachid.donation.R;
import daniarachid.donation.UserAccount.UserProfile;

public class chatActivity extends AppCompatActivity {
    String senderId, receiverId, message, userName;
    EditText mMessage;
    ImageView send;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ActionBar actionBar;
    RecyclerView messagesRec;
    MessageViewModel viewModel;
    MessageAdapter adapter;

    public chatActivity() {}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);

        //get the user name
        Intent intent = getIntent();
        fAuth = FirebaseAuth.getInstance();
        //senderId = intent.getStringExtra("senderId");
        receiverId = intent.getStringExtra("receiverId");


        fStore = FirebaseFirestore.getInstance();
        DocumentReference df = fStore.collection("Users").document(receiverId);
        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {


                DocumentSnapshot doc = task.getResult();
                if(doc.exists()) {
                    userName = doc.getString("name");
                    actionBar = getSupportActionBar();
                    actionBar.setTitle(userName);
                    actionBar.setDisplayHomeAsUpEnabled(true);
                } else {
                    Log.d("CheckMe", "Not Found");
                }


            }
        });



        senderId = intent.getStringExtra("senderId");
        Log.d("CheckMe", "Sender id from intent : " + senderId);

        mMessage = findViewById(R.id.txtMessage);
        send = findViewById(R.id.send);
        adapter = new MessageAdapter();
        messagesRec = findViewById(R.id.messagesRec);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setStackFromEnd(true);
        messagesRec.setLayoutManager(llm);

        //viewModel
        //Bundle extra = getIntent().getExtras();





        viewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        viewModel.getMessageFromFirestore(receiverId);
        viewModel.returnMessages().observe(this, new Observer<List<MessageModel>>() {
            @Override
            public void onChanged(List<MessageModel> messageModels) {

                adapter.setMessageModelList(messageModels);
                messagesRec.setAdapter(adapter);
            }
        });



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = mMessage.getText().toString();
                if (!message.isEmpty()) {
                    sendMessage(senderId, receiverId, message);

                }

            }
        });



        //messagesRec.setAdapter(adapter);


    }


    @Override
    protected void onStop() {
        super.onStop();
        viewModel.resetAll();
    }

    private void sendMessage(String senderId, String receiverId, String message) {
        //send the message
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String currentTime = formatter.format(date);


        Map<String, Object> msg = new HashMap<>();
        msg.put("sender", senderId);
        msg.put("receiver", receiverId);
        msg.put("message", message);
        msg.put("time", currentTime);
        fStore.collection("Messages").document(currentTime).set(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {

                Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //HANDLE OPTION MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.searchIcon);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.signout: signout();
                break;
            case R.id.search: //search
                break;
            case R.id.userProfile:
                startActivity(new Intent(getApplicationContext(), UserProfile.class));
                break;
            case R.id.donationRequestsRec:
                startActivity(new Intent(getApplicationContext(), TestReceiverRequestList.class));
                break;
            case android.R.id.home:
                this.finish();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    public void signout() {

        fAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();

    }
}