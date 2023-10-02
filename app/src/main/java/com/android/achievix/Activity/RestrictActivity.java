package com.android.achievix.Activity;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.achievix.Adapter.AppList;
import com.android.achievix.Adapter.ViewPagerAdapter;
import com.android.achievix.Fragments.RestrictAppList;
import com.android.achievix.Fragments.RestrictedAppList;
import com.android.achievix.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class RestrictActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restrict);

        Button b12345=findViewById(R.id.b12347);

        b12345.setOnClickListener(view -> finish());
        loadFragment();
    }

    public void loadFragment(){
        tabLayout = findViewById(R.id.tablayout_restrict);
        viewPager = findViewById(R.id.viewPager_restrict);
        viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new RestrictAppList(),"APPS");
        viewPagerAdapter.addFragment(new RestrictedAppList(),"RESTRICTED APPS");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public ArrayList<AppList> getInstalledApps() {
        ArrayList<AppList> apps = new ArrayList<>();
        @SuppressLint("QueryPermissionsNeeded") List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            PackageManager packageManager=getPackageManager();
            if (packageManager.getLaunchIntentForPackage(p.packageName)!=null) {
                String appName = p.applicationInfo.loadLabel(getPackageManager()).toString();
                Drawable icon = p.applicationInfo.loadIcon(getPackageManager());
                String packageName=p.applicationInfo.packageName;
                apps.add(new AppList(appName, icon, packageName));
            }
        }
        Collections.sort(apps, new Comparator<AppList>() {
            @Override
            public int compare(AppList o1, AppList o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return apps;
    }

    public void showHelpRestrict(View view){
        Toast.makeText(this,"Click or Hold The App You Want To Restrict.", Toast.LENGTH_SHORT).show();
    }
}
