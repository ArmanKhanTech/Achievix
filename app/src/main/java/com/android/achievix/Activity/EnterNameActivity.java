package com.android.achievix.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.achievix.R;

public class EnterNameActivity extends AppCompatActivity {

    EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_name);
        name=findViewById(R.id.editText);
    }

    public void finish(View v){
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        String n=name.getText().toString();
        myEdit.putString("name",n);
        myEdit.commit();
        Intent i=new Intent(this,MainActivity.class);
        startActivity(i);
        finish();
    }
}