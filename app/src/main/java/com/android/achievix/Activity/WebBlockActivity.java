package com.android.achievix.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.achievix.Adapter.AppAdapter;
import com.android.achievix.Adapter.AppList;
import com.android.achievix.Database.AnalysisDatabase;
import com.android.achievix.Database.Packages;
import com.android.achievix.Database.BlockWebsite;
import com.android.achievix.R;
import com.android.achievix.Service.LogURLService;

import java.util.ArrayList;

public class WebBlockActivity extends AppCompatActivity {

    BlockWebsite db;
    AnalysisDatabase db3;
    ArrayList<String> packs2,packs;
    TextView tv;
    ArrayList<AppList> list=new ArrayList<>();
    ListView ls;
    AppAdapter appAdapter;
    Packages db2;
    ArrayList<String> webList=new ArrayList<>();
    Button btnSet,btnSet1;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_block);
        btnSet = findViewById(R.id.BTN2);
        btnSet1 = findViewById(R.id.BTN3);
        tv=findViewById(R.id.tvvv22);
        ls=findViewById(R.id.blocked_website);
        db2=new Packages(this);
        db3=new AnalysisDatabase(this);
        packs2= db2.readPacks();
        packs=db3.readAllAWeb();

        populateList();

        btnSet.setOnClickListener(v -> {
            if(isAccessibilitySettingsOn(this)) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                @SuppressLint("InflateParams") View popupView = layoutInflater.inflate(R.layout.popup_web_block, null);
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                dimBehind(popupWindow);
                Button b2 = popupView.findViewById(R.id.lll);
                EditText website = popupView.findViewById(R.id.website);
                b2.setOnClickListener(view2 -> {
                    if (TextUtils.isEmpty(website.getText().toString())) {
                        Toast.makeText(this, "Field Cannot Be Empty", Toast.LENGTH_SHORT).show();
                    } else {
                        String url = website.getText().toString();
                        db.addNewWebsite(url);
                        if(!packs.contains(url)){
                            db3.addWeb(url);
                        }
                        else{
                            db3.inWebBlocked(url);
                        }
                        popupWindow.dismiss();
                        populateList();
                    }
                });
            }
            else{
                Intent i=new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(i);
            }
        });

        btnSet1.setOnClickListener(view -> {
            for(int i=0;i<webList.size();i++){
                String u=webList.get(i);
                db.deleteWebsite(u);
                db3.inWebUnblocked(u);
            }
            webList.clear();
            setButton();
            populateList();
        });

        ls.setOnItemClickListener((adapterView, view13, i, l) -> {
            ConstraintLayout cs= view13.findViewById(R.id.ConstraintLayout);
            AppList web = appAdapter.getItem(i);
            String url=web.getName();
            if(packs2.contains(url)){
                    cs.setBackground(getResources().getDrawable(R.drawable.listview_theme));
                    db2.deletePackage(url);
                    packs2 = db2.readPacks();
                    webList.remove(url);
                    setButton();
            }
            else {
                    cs.setBackground(getResources().getDrawable(R.drawable.listview_select_theme));
                    db2.addPackage(url);
                    packs2 = db2.readPacks();
                    webList.add(url);
                    setButton();
            }
        });

        Button b12345=findViewById(R.id.b12349);
        b12345.setOnClickListener(view -> finish());
    }

    @SuppressLint("SetTextI18n")
    private void populateList(){
        @SuppressLint("UseCompatLoadingForDrawables") Drawable icon=getDrawable(R.drawable.web_icon);
        ArrayList<String> w;
        db=new BlockWebsite(this);
        w=db.readWebsites();
        list.clear();

        for (int i=0;i< w.size();i++){
            String url= w.get(i);
            list.add(new AppList(url,icon,""));
        }

        appAdapter = new AppAdapter(this,list);
        ls.setAdapter(appAdapter);
        appAdapter.notifyDataSetChanged();

        if(w.isEmpty()){
            tv.setText("No Website Blocked");
        }
        else{
            tv.setText("");
        }
    }

    private void setButton(){
        if(webList.size()>0){
            btnSet.setVisibility(View.GONE);
            btnSet1.setVisibility(View.VISIBLE);
        }
        else{
            btnSet.setVisibility(View.VISIBLE);
            btnSet1.setVisibility(View.GONE);
        }
    }

    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + LogURLService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException ignored) {
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }
        return false;
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
}