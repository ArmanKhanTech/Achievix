package com.android.achievix.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.achievix.R;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ImageView ib1, ib2, ib3, ib4;
    Button b;
    Calendar c = Calendar.getInstance();
    int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
    String greeting="";

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationMenu = findViewById(R.id.nav_menu);

        b = findViewById(R.id.nav_button);
        b.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        ib1 = findViewById(R.id.goto_block_apps);
        ib2 = findViewById(R.id.goto_block_sites);
        ib3 = findViewById(R.id.goto_block_keywords);
        ib4 = findViewById(R.id.goto_block_internet);

        TextView greet= findViewById(R.id.greet);
        greet.setText(getGreetings());

        navigationMenu.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu1 -> startActivity(new Intent(this, SettingActivity.class));
                case R.id.menu2 ->
                        startActivity(new Intent(MainActivity.this, AnalysisActivity.class));
                case R.id.menu4 ->
                        startActivity(new Intent(MainActivity.this, ParentalControlActivity.class));
                case R.id.menu5 ->
                        startActivity(new Intent(MainActivity.this, AboutActivity.class));
            }

            return false;
        });

        ib1.setOnClickListener(view12 -> {
            Intent intent = new Intent(this, AppBlockActivity.class);
            startActivity(intent);
        });

        ib2.setOnClickListener(view12 -> {
            Intent intent = new Intent(this, WebBlockActivity.class);
            startActivity(intent);
        });

        ib3.setOnClickListener(view12 -> {
            Intent intent = new Intent(this, KeywordBlockActivity.class);
            startActivity(intent);
        });

        ib4.setOnClickListener(view12 -> {
            Intent intent = new Intent(this, InternetBlockActivity.class);
            startActivity(intent);
        });
    }

    public String getGreetings() {
        if(timeOfDay >= 0 && timeOfDay < 12) {
            greeting="Good Morning";
        } else if(timeOfDay >= 12 && timeOfDay < 16) {
            greeting="Good Afternoon";
        } else if(timeOfDay >= 16 && timeOfDay < 21) {
            greeting="Good Evening";
        } else if(timeOfDay >= 21 && timeOfDay < 24) {
            greeting="Good Evening";
        }

        return greeting;
    }
}