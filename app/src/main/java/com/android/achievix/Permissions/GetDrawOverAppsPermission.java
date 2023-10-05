package com.android.achievix.Permissions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.achievix.R;

public class GetDrawOverAppsPermission extends AppCompatActivity {
    Button finish;
    TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_draw_over_apps_permission);
        status = findViewById(R.id.draw_over_apps_per);
        finish = findViewById(R.id.grant_over_apps);
        granted();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        granted();
    }

    public void granted(){
        if (Settings.canDrawOverlays(this)) {
            status.setText("Permission Granted");
            finish.setVisibility(View.GONE);
        }
    }

    public void getDrawOverAppsPermission(View view){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        startActivity(intent);
    }

    public void done(View view){
        Intent intent = new Intent(GetDrawOverAppsPermission.this, GetNotificationAccess.class);
        startActivity(intent);
        finish();
    }
}