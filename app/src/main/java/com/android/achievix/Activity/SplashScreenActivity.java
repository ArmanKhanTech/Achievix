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
        SharedPreferences dataSave = getSharedPreferences("firstLog", 0);
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        ImageView imageView = findViewById(R.id.imageView1);
        imageView.startAnimation(rotate);
        Handler handler = new Handler();
        if(Objects.equals(dataSave.getString("firstTime", ""), "no")) {
            handler.postDelayed(() -> {
                SharedPreferences sh = getSharedPreferences("PASS_CODE", Context.MODE_PRIVATE);
                int i = sh.getInt("pass",0 );
                Intent intent;
                if(i>0){
                    intent = new Intent(this, EnterPasswordActivity.class);
                }
                else{
                    intent = new Intent(this, MainActivity.class);
                }
                startActivity(intent);
                finish();
            }, 3000);
        }
        else{
            SharedPreferences.Editor editor=dataSave.edit();
            editor.putString("firstTime","no");
            editor.apply();
            handler.postDelayed(() -> {
                Intent intent = new Intent(SplashScreenActivity.this, GetUsageStatsPermissionActivity.class);
                startActivity(intent);
                finish();
            }, 3000);
        }
    }
}
