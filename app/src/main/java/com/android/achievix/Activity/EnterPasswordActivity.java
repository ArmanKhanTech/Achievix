package com.android.achievix.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.achievix.R;

public class EnterPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);
        EditText edt22=findViewById(R.id.edt22);
        ImageButton bbb=findViewById(R.id.bbb);
        bbb.setOnClickListener(view -> {
            String temp=edt22.getText().toString();
            if(!temp.isEmpty()) {
                int pass = Integer.parseInt(temp);
                SharedPreferences sh = getSharedPreferences("PASS_CODE", Context.MODE_PRIVATE);
                int i = sh.getInt("pass", 0);
                if (pass == i) {
                    Intent in = new Intent(this, MainActivity.class);
                    startActivity(in);
                    finish();
                } else {
                    Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}