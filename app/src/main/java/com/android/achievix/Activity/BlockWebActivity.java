package com.android.achievix.Activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.achievix.R;

public class BlockWebActivity extends AppCompatActivity {
    String url="";
    String packages="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_web);
        url=getIntent().getStringExtra("URL");
        packages=getIntent().getStringExtra("PACKAGE");
        TextView tv= findViewById(R.id.textView22);
        tv.setText(url);
    }

    public void exit2(View view){
        String urlString = "http://focusonme.com";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setPackage(packages);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            intent.setPackage(null);
            startActivity(intent);
        }
        finish();
    }
}