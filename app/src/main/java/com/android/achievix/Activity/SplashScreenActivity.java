package com.android.achievix.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.achievix.Permissions.GetDrawOverAppsPermission;
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
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        ImageView imageView = findViewById(R.id.splash_icon);
        imageView.startAnimation(rotate);
        Handler handler = new Handler();
        if(Objects.equals(sharedPref.getString("firstTime", ""), "no")) {
            handler.postDelayed(() -> {
                int i = sharedPref.getInt("password", 0);
                Intent intent;
                if(i != 0 ){
                    intent = new Intent(this, EnterPasswordActivity.class);
                }
                else{
                    intent = new Intent(this, GetDrawOverAppsPermission.class);
                }
                startActivity(intent);
                finish();
            }, 3000);
        }
        else{
            handler.postDelayed(() -> {
                Intent intent = new Intent(SplashScreenActivity.this, GetUsageStatsPermissionActivity.class);
                startActivity(intent);
                finish();
            }, 3000);
        }
    }
}
