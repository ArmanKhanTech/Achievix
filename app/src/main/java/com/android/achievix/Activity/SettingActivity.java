package com.android.achievix.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.achievix.R;
import com.android.achievix.Service.AdminReceiver;
import com.android.achievix.Service.ForegroundService;

public class SettingActivity extends AppCompatActivity {

    LinearLayout l1,l2,l3;
    private DevicePolicyManager mgr=null;
    private ComponentName cn=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        l1=findViewById(R.id.lll1);
        l2=findViewById(R.id.lll2);
        l3=findViewById(R.id.lll3);

        cn=new ComponentName(this, AdminReceiver.class);
        mgr=(DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);

        Button b2=findViewById(R.id.bbb122);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void changeName(View v){
        Intent i=new Intent(this,EnterNameActivity.class);
        startActivity(i);
    }

    public void uninstallProtection(View v){
        Intent intent=
                new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn);
        startActivity(intent);
    }

    public void repair(View v){
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service is Running");
        ContextCompat.startForegroundService(this, serviceIntent);
        Toast.makeText(this,"App Repaired",Toast.LENGTH_SHORT).show();
    }
}