package daniarachid.donation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import daniarachid.donation.DonationRequestManagement.ReceiverRequestsList;
import daniarachid.donation.UserAccount.UserProfile;
import io.grpc.NameResolver;

public class chatActivity extends AppCompatActivity {
    String senderId, receiverId, message, userName;
    EditText mMessage;
    ImageView send;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ActionBar actionBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);

        //get the user name
        Intent intent = getIntent();
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


        fAuth = FirebaseAuth.getInstance();
        senderId = fAuth.getCurrentUser().getUid();
        mMessage = findViewById(R.id.txtMessage);
        send = findViewById(R.id.send);
        //Bundle extra = getIntent().getExtras();


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = mMessage.getText().toString();
                if (!message.isEmpty()) {
                    sendMessage(senderId, receiverId, message);

                }

            }
        });




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
                startActivity(new Intent(getApplicationContext(), ReceiverRequestsList.class));
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
        finish();

    }
}