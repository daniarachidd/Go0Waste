package daniarachid.donation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentProviderClient;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {
    EditText mEmail, mPassword;
    //Button btnLogin;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmail = findViewById(R.id.txtEmail);
        mPassword = findViewById(R.id.txtPassword);
        progressBar = findViewById(R.id.progressBar2);
        fAuth = FirebaseAuth.getInstance();
        // if already signed in --> redirect user to main donation activity.




    }

    public void resetPassword(View view) {
        // GET USER EMAIL

        EditText resetMail = new EditText(view.getContext());
        AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
        passwordResetDialog.setTitle("Reset password ?");
        passwordResetDialog.setMessage("Enter Your Email To Receive a Reset Link ");

        passwordResetDialog.setView(resetMail);
        passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // SEND RESET lINK
                String mail = resetMail.getText().toString();
                fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity.this, "Reset Link has been sent to your Email. ", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error: Link could not be sent. " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


            }
        });
        passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // BACK TO LOGIN VIEW
            }
        });

        passwordResetDialog.create().show();
    }
    public void signIn(View view){

/**
        if(fAuth.getCurrentUser() != null ) {
            startActivity(new Intent(getApplicationContext(), TestMainDonation.class));
            finish();
        } **/
        // INPUT VALIDATION
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required.");
                    return;
                }

                if(TextUtils.isEmpty(password)) {
                    mPassword.setError("password is required.");
                    return;
                }



                progressBar.setVisibility(View.VISIBLE);

                // USER AUTHENTICATION
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NotNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Signed in successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), TestMainDonation.class));
                            progressBar.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(MainActivity.this, "Error: " + task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }


                });

            }

        //SIGN IN


    public void signUp(View v){
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(intent);

    }

}