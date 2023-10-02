package com.android.achievix.Permissions;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.achievix.Activity.MainActivity;
import com.android.achievix.R;

public class GetUsageStatsPermissionActivity extends AppCompatActivity {
    int mode;
    Button grant,next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_usage_stats_permission);
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        permitted();
    }

    public void permitted(){
        TextView tv = findViewById(R.id.tv);
        grant= findViewById(R.id.grant);
        next= findViewById(R.id.next);
        if(checkForPermission(this)) {
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    tv.setText("Permission Granted");
                    grant.setVisibility(View.GONE);
                    next.setVisibility(View.GONE);
                }
                else{
                    tv.setText("Permission Granted");
                    grant.setVisibility(View.GONE);
                }
            }
            else{
                tv.setText("Permission Granted");
                grant.setVisibility(View.GONE);
                next.setVisibility(View.GONE);
            }
        }
    }

    private boolean checkForPermission(Context context){
        AppOpsManager appOps=(AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        mode=appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,android.os.Process.myUid(),context.getPackageName());
        return mode==AppOpsManager.MODE_ALLOWED;
    }

    public void getUsagePermission(View view){
        Intent intent=new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);
    }

    public void next(View view){
        Intent intent=new Intent(GetUsageStatsPermissionActivity.this,GetDrawOverAppsPermission.class);
        startActivity(intent);
        finish();
    }

    public void main(View view){
        Intent intent=new Intent(GetUsageStatsPermissionActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

}