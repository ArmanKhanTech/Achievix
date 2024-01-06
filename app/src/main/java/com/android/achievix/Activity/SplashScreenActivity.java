package com.android.achievix.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.achievix.Permissions.GetUsageStatsPermissionActivity;
import com.android.achievix.R;

import java.util.Objects;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_splash_screen);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        SharedPreferences sharedPref = getSharedPreferences("achievix", MODE_PRIVATE);

        Handler handler = new Handler();
        if(Objects.equals(sharedPref.getString("firstTime", "yes"), "no")) {
            handler.postDelayed(() -> {
                int i = sharedPref.getInt("password", 0);
                Intent intent;

                if(i != 0 ) {
                    intent = new Intent(this, EnterPasswordActivity.class);
                }
                else {
                    intent = new Intent(this, MainActivity.class);
                }
                startActivity(intent);
                finish();
            }, 1800);
        }
        else {
            handler.postDelayed(() -> {
                Intent intent = new Intent(SplashScreenActivity.this, GetUsageStatsPermissionActivity.class);
                startActivity(intent);
                finish();
            }, 1800);
        }
    }
}
