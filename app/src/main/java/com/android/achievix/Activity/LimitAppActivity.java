package com.android.achievix.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.achievix.Database.AnalysisDatabase;
import com.android.achievix.Database.LimitPackages;
import com.android.achievix.R;
import com.android.achievix.Service.ForegroundService;
import com.android.achievix.Utility.GetUsageInfo;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class LimitAppActivity extends AppCompatActivity {

    LimitPackages db=new LimitPackages(this);
    AnalysisDatabase db2;
    ArrayList<String> packs=new ArrayList<>();
    String packageName="";
    int id,pos;
    String category;
    float daily,weekly,monthly,yearly,data;
    TextView t6,t21,t13,t14,xy1,xyz1,t18;
    Context context;
    GetUsageInfo gui;
    BarChart barChart;
    LinearLayout ly1;
    static long DAY_IN_MILLIS = 86400 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_limit_app);
        context=getApplicationContext();
        gui=new GetUsageInfo(context);
        id=getIntent().getIntExtra("POS",0);
        String appName=getIntent().getStringExtra("APP_NAME");
        packageName=getIntent().getStringExtra("PACKAGE_NAME");
        TextView app_name= findViewById(R.id.app_name);
        barChart = findViewById(R.id.barChart_view);
        t6= findViewById(R.id.textView6);
        t21= findViewById(R.id.textView21);
        t13= findViewById(R.id.textView13);
        t14= findViewById(R.id.textView14);
        t18= findViewById(R.id.textView18);
        xy1= findViewById(R.id.xy1);
        xyz1= findViewById(R.id.xyz1);
        ly1=findViewById(R.id.ly1);
        app_name.setText(appName);
        ImageView appIcon= findViewById(R.id.app_icon);
        try {
            Drawable icon = getPackageManager().getApplicationIcon(packageName);
            appIcon.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        db2=new AnalysisDatabase(context);
        packs=db2.readAllApps();

        new loadData().execute();

        Button b12345=findViewById(R.id.b12346);

        b12345.setOnClickListener(view -> finish());
    }

    public void loadGraph(){
        ArrayList<BarEntry> entries = new ArrayList<>();
        //needs fixing
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long startMillis = calendar.getTimeInMillis();
        long endMillis = System.currentTimeMillis();

        for (int daysAgo = 0; daysAgo < 7; daysAgo++) {
            float usageTime=gui.fetchAppStatsInfo(startMillis, endMillis, packageName);
            if (usageTime<(float)0){
                usageTime = 0;
            }
            entries.add(new BarEntry(usageTime, 7 - daysAgo - 1));
            startMillis -= DAY_IN_MILLIS;
            endMillis -= DAY_IN_MILLIS;
        }

        ArrayList<String> labels = new ArrayList<>();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM");
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -6);
        for (int i = 0; i < 7; i++) {
            labels.add(dateFormat.format(calendar.getTime()));
            calendar.add(Calendar.DATE, 1);
        }

        String title = "Weekly Usage Stats (in Hours)";

        BarDataSet barDataSet = new BarDataSet(entries, title);
        barDataSet.setColor(getResources().getColor(R.color.purple_500));

        BarData data = new BarData(labels,barDataSet);
        barChart.setData(data);
        barChart.setDescription(null);
        barChart.getData().setValueFormatter(new TimeFormatter());
        barChart.getXAxis().setLabelsToSkip(0);
        barChart.getAxisLeft().setValueFormatter(new LargeValueFormatter());
        barChart.animateXY(2000, 2000);
        barChart.invalidate();
    }

    @SuppressLint("StaticFieldLeak")
    private class loadData extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ly1.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getStatsInfo(packageName);
            getCategoryTitle();
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Void results) {
            super.onPostExecute(results);
            ly1.setVisibility(View.GONE);
            t6.setText(String.valueOf(daily));
            t21.setText(String.valueOf(weekly));
            t13.setText(String.valueOf(monthly));
            t14.setText(String.valueOf(yearly));
            xyz1.setText(category);
            if(category==null){
                xyz1.setText("Other");
            }
            xy1.setText(String.valueOf(pos));
            if(pos<10){
                String p="0"+pos;
                xy1.setText(p);
            }
            if(pos>99){
                xy1.setText("99+");
                xy1.setTextSize(18);
            }
            t18.setText(String.valueOf(data));
            loadGraph();
        }
    }

    public void limitApp(View view){
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = layoutInflater.inflate(R.layout.popup_limit, null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        dimBehind(popupWindow);
        Button b2= popupView.findViewById(R.id.ll);
        EditText hrs= popupView.findViewById(R.id.hrs);
        EditText mins= popupView.findViewById(R.id.mins);
        b2.setOnClickListener(view1 -> {
            if(TextUtils.isEmpty(hrs.getText().toString())){
                    Toast.makeText(context, "Field Cannot Be Empty", Toast.LENGTH_SHORT).show();
            }
            else if(TextUtils.isEmpty(mins.getText().toString())) {
                Toast.makeText(context, "Field Cannot Be Empty", Toast.LENGTH_SHORT).show();
            }
            else {
                String value = hrs.getText().toString();
                int h = Integer.parseInt(value);
                value = mins.getText().toString();
                int m = Integer.parseInt(value);
                long m1 = (long) h * 60 * 60 * 1000;
                long m2 = (long) m * 60 * 1000;
                long m3 = m1 + m2;
                String duration = String.valueOf(m3);
                db.addNewPackageInLimit(packageName, duration);
                startService();
                popupWindow.dismiss();
                if(!packs.contains(packageName)){
                    db2.addApp(packageName);
                }
                else {
                    db2.inAppLimit(packageName);
                }
                finish();
            }
        });
    }

    private void startService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service is Running");
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    private void getStatsInfo(String pkgName) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startMillis;
        long endMillis = System.currentTimeMillis();

        calendar.set(Calendar.HOUR_OF_DAY, 1);
        startMillis = calendar.getTimeInMillis();
        daily=gui.fetchAppStatsInfo(startMillis, endMillis, pkgName);
        data=gui.getPkgInfo(startMillis, endMillis, packageName);

        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        startMillis = calendar.getTimeInMillis();
        weekly=gui.fetchAppStatsInfo(startMillis, endMillis, pkgName);
        pos= (int) gui.getRank(startMillis, endMillis, packageName);

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        startMillis = calendar.getTimeInMillis();
        monthly=gui.fetchAppStatsInfo(startMillis, endMillis, pkgName);

        calendar.set(Calendar.DAY_OF_YEAR, 1);
        startMillis = calendar.getTimeInMillis();
        yearly=gui.fetchAppStatsInfo(startMillis, endMillis, pkgName);
    }

    private void getCategoryTitle(){
        PackageManager pm = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = pm.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int appCategory = Objects.requireNonNull(applicationInfo).category;
            category = (String) ApplicationInfo.getCategoryTitle(context, appCategory);
        }
    }

    public static void dimBehind(PopupWindow popupWindow) {
        View container = popupWindow.getContentView().getRootView();
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.5f;
        wm.updateViewLayout(container, p);
    }

    private static class TimeFormatter implements ValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            if(value == 0) {
                return "0s";
            }
            String hour = String.valueOf(value);
            String time = "";
            time += hour + " hrs";
            return time;
        }
    }
}
