package com.android.achievix.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.achievix.Adapter.AppList;
import com.android.achievix.Adapter.ViewPagerAdapter;
import com.android.achievix.Fragments.DataLimitAppList;
import com.android.achievix.Fragments.DataLimitedAppList;
import com.android.achievix.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InternetBlockActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    Button b12345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_block);

        b12345=findViewById(R.id.b123455);

        b12345.setOnClickListener(view -> finish());

        loadFragment();
    }

    private void loadFragment(){
        tabLayout = findViewById(R.id.tablayout_internet);
        viewPager = findViewById(R.id.viewPager_internet);
        viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new DataLimitAppList(),"APPS");
        viewPagerAdapter.addFragment(new DataLimitedAppList(),"LIMITED APPS");
        viewPager.setOffscreenPageLimit(0);
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

    public void showHelpInternet(View view){
        Toast.makeText(this,"Click The App Whose Data You Want To Limit.", Toast.LENGTH_SHORT).show();
    }
}