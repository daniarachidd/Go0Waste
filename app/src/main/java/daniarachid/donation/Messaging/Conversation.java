package daniarachid.donation.Messaging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daniarachid.donation.Adapters.MessageAdapter;
import daniarachid.donation.Entity.MessageModel;
import daniarachid.donation.UserAccount.MainActivity;
import daniarachid.donation.Notification.APISERVICE;
import daniarachid.donation.Notification.Client;
import daniarachid.donation.Notification.Data;
import daniarachid.donation.Notification.Response;
import daniarachid.donation.Notification.Sender;
import daniarachid.donation.Notification.Token;
import daniarachid.donation.R;
import retrofit2.Call;
import retrofit2.Callback;

public class Conversation extends AppCompatActivity {
    String senderId, receiverId, message, userName;

    EditText mMessage;
    ImageView send;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ActionBar actionBar;
    RecyclerView messagesRec;
    MessageViewModel viewModel;
    MessageAdapter adapter;
    String nameOfSender, token, userIdForToken;
    APISERVICE apiservice;
    boolean notify = false;

    ListenerRegistration registration;


    public Conversation() {}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_conversation);


        //create api service
        apiservice = Client.getRetrofit("https://fcm.googleapis.com/").create(APISERVICE.class);

        //get the user name
        Intent intent = getIntent();
        fAuth = FirebaseAuth.getInstance();

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


        mMessage = findViewById(R.id.txtMessage);
        send = findViewById(R.id.send);
        adapter = new MessageAdapter(senderId, receiverId);
        messagesRec = findViewById(R.id.messagesRec);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setStackFromEnd(true);
        messagesRec.setLayoutManager(llm);



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
                notify = true;
                message = mMessage.getText().toString();
                if (!message.isEmpty()) {
                    sendMessage(senderId, receiverId, message);
                    mMessage.setText("");

                }

            }
        });





    }


    @Override
    protected void onStop() {
        super.onStop();
        if (registration != null) {
            registration.remove();
        }

        viewModel.resetAll();
    }

    private void sendMessage(String senderId, String receiverId, String message) {

        //send the message
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
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

                String msg = message;
                Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_SHORT).show();
            }
        });

        fStore.collection("Users").document(senderId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {


                nameOfSender = documentSnapshot.getString("name");
                if (notify) {
                    sendNotification(receiverId, nameOfSender, message);
                    //Log.d("CheckMe", "Notify: " + nameOfSender);

                }

                notify = false;


            }
        });
    }

    private void sendNotification(String receiverId, String nameOfSender, String message) {

        fAuth = FirebaseAuth.getInstance();
        userIdForToken = fAuth.getCurrentUser().getUid();
        DocumentReference df = fStore.collection("Tokens").document(receiverId);
        registration = df.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                assert value != null;
                Token objectToken = value.toObject(Token.class);
                assert objectToken != null;
                token = objectToken.getToken();


                Log.d("CheckMe", "Chat token" + token);
                Data data = new Data(userIdForToken,
                        R.drawable.notification, message, "New message from " + nameOfSender, receiverId, 1);

                Sender sender = new Sender(data, token);

                apiservice.sendNotification(sender).enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                        Toast.makeText(getApplicationContext(), ""+response.message(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Not sent", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });


    }


    //HANDLE OPTION MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_chat, menu);
        MenuItem menuItem = menu.findItem(R.id.searchIcon);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.deleteChat: deleteChat();
                break;
            case android.R.id.home:
                this.finish();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteChat() {
        //delete chat
        MessageViewModel viewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        viewModel.deleteMessageFromFireStore(receiverId);
        onStop();
        startActivity(new Intent(getApplicationContext(), Chat.class));
    }

    public void signout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();;

    }
}