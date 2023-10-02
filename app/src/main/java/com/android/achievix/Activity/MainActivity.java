package com.android.achievix.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.achievix.R;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ImageButton ib1,ib2,ib3,ib4,ib5;
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

        ib1= findViewById(R.id.imageButton);
        ib2= findViewById(R.id.imageButton2);
        ib3= findViewById(R.id.imageButton3);
        ib4= findViewById(R.id.imageButton4);
        ib5= findViewById(R.id.imageButton5);

        TextView greet= findViewById(R.id.greet);
        greet.setText(getGreetings());

        TextView name= findViewById(R.id.name);
        SharedPreferences sh = this.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String s1 = sh.getString("name", "");
        name.setText(s1);

        navigationMenu.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu1:
                    startActivity(new Intent(this, SettingActivity.class));
                    break;

                case R.id.menu2:
                    startActivity(new Intent(MainActivity.this, AnalysisActivity.class));
                    break;

                case R.id.menu4:
                    startActivity(new Intent(MainActivity.this, ParentalControlActivity.class));
                    break;

                case R.id.menu5:
                    startActivity(new Intent(MainActivity.this, AboutActivity.class));
                    break;
            }
            return false;
        });

        ib1.setOnClickListener(view1 -> {
            Intent intent=new Intent(this, RestrictActivity.class);
            startActivity(intent);
        });
        ib2.setOnClickListener(view12 -> {
            Intent intent=new Intent(this, LimitActivity.class);
            startActivity(intent);
        });
        ib3.setOnClickListener(view12 -> {
            Intent intent=new Intent(this, WebBlockActivity.class);
            startActivity(intent);
        });
        ib4.setOnClickListener(view12 -> {
            Intent intent=new Intent(this, InternetBlockActivity.class);
            startActivity(intent);
        });
        ib5.setOnClickListener(view12 -> {
            Intent intent=new Intent(this, JobReminderActivity.class);
            startActivity(intent);
        });
    }

    public String getGreetings(){
        if(timeOfDay >= 0 && timeOfDay < 12){
            greeting="Good Morning.";
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            greeting="Good Afternoon.";
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            greeting="Good Evening.";
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            greeting="Good Evening.";
        }
        return greeting;
    }

}