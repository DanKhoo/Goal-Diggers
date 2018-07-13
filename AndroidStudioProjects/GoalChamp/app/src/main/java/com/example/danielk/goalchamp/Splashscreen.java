package com.example.danielk.goalchamp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.danielk.goalchamp.AccountActivity.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class Splashscreen extends AppCompatActivity {

    Handler handler;
    TextView mSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        mSplash = findViewById(R.id.tvSplash);
        Typeface myCustomFont = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-ExtraLightItalic.ttf");
        mSplash.setTypeface(myCustomFont);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;

                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    intent = new Intent(Splashscreen.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    intent = new Intent(Splashscreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },500);
    }
}
