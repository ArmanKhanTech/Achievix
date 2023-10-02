package com.android.achievix.Permissions;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.achievix.Activity.EnterNameActivity;
import com.android.achievix.R;

public class GetNotificationAccess extends AppCompatActivity {

    Button finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_notification_access);
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        granted();
    }

    public void granted(){
        TextView tv1= findViewById(R.id.tv11);
        finish= findViewById(R.id.grant11);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (Settings.Secure.getString(this.getContentResolver(),"enabled_notification_listeners").contains(getApplicationContext().getPackageName())) {
                tv1.setText("Permission Granted");
                finish.setVisibility(View.GONE);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getNotificationAccess(View view){
        Intent intent=new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        startActivity(intent);
    }

    public void done11(View view){
        Intent intent = new Intent(GetNotificationAccess.this, EnterNameActivity.class);
        startActivity(intent);
        finish();
    }
}
