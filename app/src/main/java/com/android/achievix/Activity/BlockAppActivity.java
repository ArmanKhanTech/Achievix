package com.android.achievix.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.achievix.R;

public class BlockAppActivity extends AppCompatActivity {
    String packName="";
    String msg="This App Is Blocked By FocusOnMe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_app);
        packName=getIntent().getStringExtra("PACK_NAME");
        msg=getIntent().getStringExtra("MSG");
        TextView tv= findViewById(R.id.textView22);
        TextView tv1= findViewById(R.id.textView33);
        tv1.setText(msg);
        PackageManager packageManager= getApplicationContext().getPackageManager();
        String appName = "";
        try {
            appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packName, PackageManager.GET_META_DATA));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        tv.setText(appName);
        ImageView appIcon= findViewById(R.id.imageView);
        try {
            Drawable icon = getPackageManager().getApplicationIcon(packName);
            appIcon.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void exit(View view){
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startMain);
        finish();
    }
}
