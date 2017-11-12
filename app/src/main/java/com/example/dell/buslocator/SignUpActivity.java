package com.example.dell.buslocator;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private EditText mEmail;
    private EditText mName;
    private EditText mPassword;
    private EditText mContact;


    private EditText mConfirmPassword;

    DatabaseReference databaseUser;

    private Button mSignUp;

    private FirebaseAuth mAuth;

    private static final String TAG = "EmailPassword";

    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        mEmail = (EditText)findViewById(R.id.etEmailAddress);
        mPassword = (EditText)findViewById(R.id.etSignUpPassword);
        mConfirmPassword = (EditText)findViewById(R.id.etSignUpConfirmPassword);
        mName = (EditText)findViewById(R.id.etName);
        mContact = (EditText)findViewById(R.id.etContact);

        mSignUp = (Button)findViewById(R.id.btSignUp);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                String contact = mContact.getText().toString();
                String name = mName.getText().toString();
                createAccount(name,email,password,contact);
            }
        });

        /*mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    sendEmailVerification();
                } else {
                    // User is signed out

                }
                // ...
            }
        };*/

    }


    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    private void createAccount(final String name,final String email,final String password,final String contact) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(SignUpActivity.this, "User successfully registered", Toast.LENGTH_SHORT).show();
                            addUser(name,email,contact);
                            startActivity(new Intent(SignUpActivity.this, EmailVerificationActivity.class));
                            //finish();

                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
    }

    private void sendEmailVerification() {

        final FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(SignUpActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String contact = mContact.getText().toString();
        if (TextUtils.isEmpty(contact)) {
            mContact.setError("Required.");
            valid = false;
        } else {
            mContact.setError(null);
        }

        String email = mEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Required.");
            valid = false;
        } else {
            mEmail.setError(null);
        }

        String password = mPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Required.");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        String confirmPassword = mConfirmPassword.getText().toString();
        if (TextUtils.isEmpty(confirmPassword)) {
            mConfirmPassword.setError("Required.");
            valid = false;
        } else {
            mConfirmPassword.setError(null);
        }

        if(!(password.equals(confirmPassword))){
            valid = false;
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        }

        return valid;
    }

    public void addUser(String name,String email, String contact){
        Log.i("check: " , "inside addUser");
        databaseUser = FirebaseDatabase.getInstance().getReference("User");
        String id = databaseUser.push().getKey();
        Log.i("userid:" , id + "");
        User user = new User(name,email,contact,id);
        databaseUser.child(id).setValue(user);

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
