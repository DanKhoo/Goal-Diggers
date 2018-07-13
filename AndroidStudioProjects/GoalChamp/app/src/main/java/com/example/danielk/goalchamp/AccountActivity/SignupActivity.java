package com.example.danielk.goalchamp.AccountActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.danielk.goalchamp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignupActivity extends AppCompatActivity {

    private TextView header;
    private EditText mEditUsername;
    private EditText mEditTextEmail;
    private EditText mEditTextPw;
    private EditText mEditTextRePw;
    private Button mButtonSignup;
    private TextView mLogin;
    private FirebaseAuth auth;
    String username, email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        header = findViewById(R.id.header);
        mEditUsername = findViewById(R.id.editTextUsername);
        mEditTextEmail = findViewById(R.id.editTextEmail);
        mEditTextPw = findViewById(R.id.editTextPassword);
        mEditTextRePw = findViewById(R.id.editTextRePassword);
        mButtonSignup = findViewById(R.id.buttonSignup);
        mLogin = findViewById(R.id.viewLogin);

        Typeface myCustomFont = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Regular.ttf");
        header.setTypeface(myCustomFont);
        mEditUsername.setTypeface(myCustomFont);
        mEditTextEmail.setTypeface(myCustomFont);
        mEditTextPw.setTypeface(myCustomFont);
        mEditTextRePw.setTypeface(myCustomFont);
        mLogin.setTypeface(myCustomFont);

        // Firebase Auth Instance
        auth = FirebaseAuth.getInstance();

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        mButtonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = mEditUsername.getText().toString().trim();
                email = mEditTextEmail.getText().toString().trim();
                String password = mEditTextPw.getText().toString().trim();
                String repassword = mEditTextRePw.getText().toString().trim();

                // check if parameters is empty
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(SignupActivity.this, "Enter Username", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignupActivity.this, "Enter Email Address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignupActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(repassword)) {
                    Toast.makeText(SignupActivity.this, "Enter Retype Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                //check if email is valid
                if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
                    Toast.makeText(SignupActivity.this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
                    return;
                }
                //check if password is alphanumeric
                if (!password.matches("^(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z])[a-zA-Z0-9]+$")) {
                    Toast.makeText(SignupActivity.this, "Password must contain one upper and lower case letter and one number", Toast.LENGTH_LONG).show();
                    return;
                }
                //check if password and retype password is the same
                if (!password.equals(repassword)) {
                    Toast.makeText(SignupActivity.this, "Password Not Matching", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.createUserWithEmailAndPassword(email, password).
                        addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Email has been used", Toast.LENGTH_SHORT).show();
                                } else {
                                    sendEmailVerification();
                                }
                            }
                        });

            }
        });
    }

    // Send user email verification when he sign up
    private void sendEmailVerification() {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        sendUserData();
                        Toast.makeText(SignupActivity.this, "Successfully Registered, Verification email sent!", Toast.LENGTH_LONG).show();
                        auth.signOut();
                        finish();
                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                    } else {
                        Toast.makeText(SignupActivity.this, "Verification email has'nt been sent!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    // Send User Data to Database
    private void sendUserData() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("users").child(auth.getUid());
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        databaseReference.setValue(user);
    }
}
