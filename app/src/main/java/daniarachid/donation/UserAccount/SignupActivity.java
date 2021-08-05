package daniarachid.donation.UserAccount;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import daniarachid.donation.R;

public class SignupActivity extends AppCompatActivity {
    //mName , mEmail, mPassword, confirm password
    EditText mName, mEmail, mPassword, mCPassword,mPhoneNo;


    Button signUpBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userId, token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    private void saveToken(String token) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        userId =  fAuth.getCurrentUser().getUid();
        Map<String, Object> tokens = new HashMap<>();
        tokens.put("token", token);
        fStore.collection("Tokens").document(userId).set(tokens).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });


    }
    public void signInClicked(View v){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    public void signUp(View view) {
        mName = findViewById(R.id.txtName);
        mEmail = findViewById(R.id.txtEmail);
        mPassword = findViewById(R.id.txtPassword);
        mCPassword = findViewById(R.id.txtCPassword);
        mPhoneNo = findViewById(R.id.txtPhone);

        signUpBtn = findViewById(R.id.btnUpdate);

        //get the current instance of the database
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar2);

        String name = mName.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String cPassword = mCPassword.getText().toString().trim();
        String phoneNo = mPhoneNo.getText().toString().trim();

        //-------------------------------
        //DATA VALIDATION
        if(TextUtils.isEmpty(email)) {
            mEmail.setError("Email is required.");
            return;
        }



        if(TextUtils.isEmpty(password)) {
            mPassword.setError("password is required.");
            return;
        }

        if(TextUtils.isEmpty(cPassword)) {
            mCPassword.setError("Confirm password is required.");
            return;
        }
        if(TextUtils.isEmpty(phoneNo)) {
            mCPassword.setError("Phone Number is required.");
            return;
        }

        if(password.length() < 8) {
            mPassword.setError("Password must be > 8 characters");
            return;
        }

        if (!password.equals(cPassword)){
            mPassword.setError("Passwords do not match. ");
            mCPassword.setError("Passwords do not match");
            return;
        }
        // END OF DATA VALIDATION
        //-------------------------

        progressBar.setVisibility(View.VISIBLE);

        //-----------------
        //REGISTER THE USER
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull  Task<AuthResult> task) {

                if(task.isSuccessful()) {

                    //VERIFY USER -- 1. SEND VERIFICATION EMAIL
                    FirebaseUser fUser = fAuth.getCurrentUser();
                    fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(SignupActivity.this, "Verification email has been sent", Toast.LENGTH_SHORT).show();
                            //get the token
                            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                @Override
                                public void onSuccess(InstanceIdResult instanceIdResult) {
                                    token = instanceIdResult.getToken();
                                    saveToken(token);
                                }
                            });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Log.d("TAG", "onFailure: verification email is not sent " + e.getMessage());

                        }
                    });



                    //Toast.makeText(SignupActivity.this, "Signed up successfully", Toast.LENGTH_SHORT).show();
                    userId = fAuth.getCurrentUser().getUid();

                    // REGISTER USER INFORMATION
                    DocumentReference docReference = fStore.collection("Users").document(userId);
                    Map<String, Object> user = new HashMap<>();

                    //INSERT DATA
                    user.put("name", name);
                    user.put("email", email);
                    user.put("phone", phoneNo);
                    user.put("isAuthorized", false);
                    docReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("TAG", "onSuccess: use profile is created for " + userId);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Log.d("TAG", "onFailure: " + e.toString());
                        }
                    });
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

                } else {
                    Toast.makeText(SignupActivity.this, "Error: " + task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }

            }

    });
} 

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {

            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);


        }
}