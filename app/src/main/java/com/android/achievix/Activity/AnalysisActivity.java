package com.android.achievix.Activity;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.achievix.Adapter.ViewPagerAdapter;
import com.android.achievix.Fragments.AppAnalysisFragment;
import com.android.achievix.Fragments.WebAnalysisFragment;
import com.android.achievix.R;
import com.google.android.material.tabs.TabLayout;

public class AnalysisActivity extends AppCompatActivity {

    Button b;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);
        b=findViewById(R.id.b12350);

        tabLayout = findViewById(R.id.tablayout_ana);
        viewPager = findViewById(R.id.viewPager_ana);
        viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new AppAnalysisFragment(),"APPS");
        viewPagerAdapter.addFragment(new WebAnalysisFragment(),"WEBSITES");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        b.setOnClickListener(view -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}