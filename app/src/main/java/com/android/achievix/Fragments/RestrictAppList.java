package com.android.achievix.Fragments;
//DONE
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.achievix.Activity.RestrictActivity;
import com.android.achievix.Activity.RestrictAppActivity;
import com.android.achievix.Adapter.AppAdapter;
import com.android.achievix.Adapter.AppList;
import com.android.achievix.DataBase.AnalysisDatabase;
import com.android.achievix.DataBase.Packages;
import com.android.achievix.DataBase.SaveLimitPackages;
import com.android.achievix.DataBase.SaveRestrictPackages;
import com.android.achievix.R;
import com.android.achievix.Service.ForegroundService;

import java.util.ArrayList;

public class RestrictAppList extends Fragment {
    private ArrayList<AppList> installedApps;
    private AppAdapter installedAppAdapter;
    private ListView userInstalledApps;
    EditText editText;
    ProgressBar pBar;
    TextView pw1;
    private Button b2;
    private final ArrayList<String> packages=new ArrayList<>();
    private ArrayList<String> packs, packs1,packs2,packs3;
    SaveLimitPackages db1;
    SaveRestrictPackages db;
    Packages db2;
    AnalysisDatabase db3;
    loadApps la=new loadApps();
    View view;
    FrameLayout fll2;

    public RestrictAppList() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        la.execute();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_restrict_app_list,container,false);
        userInstalledApps= view.findViewById(R.id.installed_app_list_restrict);
        editText= view.findViewById(R.id.search_bar);
        pBar= view.findViewById(R.id.pBar2);
        pw1= view.findViewById(R.id.pw1);
        b2=view.findViewById(R.id.restrict);
        b2.setVisibility(View.GONE);
        fll2=view.findViewById(R.id.fll2);
        fll2.setVisibility(View.GONE);
        db = new SaveRestrictPackages(getActivity());
        db1 = new SaveLimitPackages(getActivity());
        db2=new Packages(getActivity());
        db3=new AnalysisDatabase(getActivity());
        packs= db.readRestrictPacks();
        packs1= db1.readLimitPacks();
        packs2=db2.readPacks();
        packs3=db3.readAllApps();

        userInstalledApps.setTextFilterEnabled(true);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                installedAppAdapter.getFilter().filter(s);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        userInstalledApps.setOnItemClickListener((adapterView, view13, i, l) -> {
            ConstraintLayout cs= view13.findViewById(R.id.ConstraintLayout);
            AppList appList = installedAppAdapter.getItem(i);
            String appName=appList.getName();
            String packageName= appList.getPackageName();
            if(packs.contains(packageName)) {
                Toast.makeText(getActivity(),appName+" Already Restricted." ,Toast.LENGTH_SHORT).show();
            }
            else if(packageName.equals("com.android.focusonme")){
                Toast.makeText(getActivity(),appName+" Cannot Be Restricted." ,Toast.LENGTH_SHORT).show();
            }
            else if(packs1.contains(packageName)){
                Toast.makeText(getActivity(),appName+" Already Limited." ,Toast.LENGTH_SHORT).show();
            }
            else{
                if(packs2.contains(packageName)){
                    cs.setBackground(getResources().getDrawable(R.drawable.listview_theme));
                    db2.deletePackage(packageName);
                    packs2 = db2.readPacks();
                    packages.remove(packageName);
                    setButtonColor();
                }
                else {
                    cs.setBackground(getResources().getDrawable(R.drawable.listview_select_theme));
                    db2.addPackage(packageName);
                    packages.add(packageName);
                    packs2 = db2.readPacks();
                    setButtonColor();
                }
            }
            installedAppAdapter.notifyDataSetChanged();
        });

        userInstalledApps.setOnItemLongClickListener((adapterView, view12, i, l) -> {
            AppList appList = installedAppAdapter.getItem(i);
            String appName=appList.getName();
            String packageName= appList.getPackageName();
            if(packs.contains(packageName)) {
                Toast.makeText(getActivity(),appName+" Already Restricted." ,Toast.LENGTH_SHORT).show();
            }
            else if(packageName.equals("com.android.focusonme")){
                Toast.makeText(getActivity(),appName+" Cannot Be Restricted." ,Toast.LENGTH_SHORT).show();
            }
            else if(packs1.contains(packageName)){
                Toast.makeText(getActivity(),appName+" Already Limited." , Toast.LENGTH_SHORT).show();
            }
            else{
                Intent intent = new Intent(getActivity(), RestrictAppActivity.class);
                intent.putExtra("APP_NAME", appName);
                intent.putExtra("PACKAGE_NAME", packageName);
                startActivity(intent);
            }
            return true;
        });

        b2.setOnClickListener(view1 -> {
            for(int i=0;i<packages.size();i++){
                String ps= packages.get(i);
                db.addNewPackageInRestrict(ps);
                db2.deletePackage(ps);
                if(!packs3.contains(ps)){
                    db3.addApp(ps);
                }
                else{
                    db3.inAppRestrict(ps);
                }
            }
            packages.clear();
            setButtonColor();
            updateList();
            startService();
        });

        return view;
    }

    private void startService() {
        Intent serviceIntent = new Intent(getActivity(), ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service is Running");
        ContextCompat.startForegroundService(requireActivity(), serviceIntent);
    }

    public void setButtonColor(){
        if(packages.size()>0){
            b2.setBackground(getResources().getDrawable(R.drawable.restrictbutton_theme));
        }
        else{
            b2.setBackground(getResources().getDrawable(R.drawable.bgbutton_theme));
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class loadApps extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pw1.setVisibility(View.VISIBLE);
            pBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            installedApps = ((RestrictActivity) requireActivity()).getInstalledApps();
            return null;
        }

        @Override
        protected void onPostExecute(Void results) {
            super.onPostExecute(results);
            pw1.setVisibility(View.GONE);
            pBar.setVisibility(View.GONE);
            b2.setVisibility(View.VISIBLE);
            fll2.setVisibility(View.VISIBLE);
            installedAppAdapter = new AppAdapter(requireActivity(),installedApps);
            userInstalledApps.setAdapter(installedAppAdapter);
            installedAppAdapter.notifyDataSetChanged();
            cancel(true);
        }
    }

    public void updateList(){
        packs= db1.readLimitPacks();
        packs1= db.readRestrictPacks();
        packs3=db3.readAllApps();
        installedAppAdapter = new AppAdapter(requireActivity(),installedApps);
        userInstalledApps.setAdapter(installedAppAdapter);
        installedAppAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        la.cancel(true);
        for(int i=0;i<packages.size();i++){
            String ps=packages.get(i);
            db2.deletePackage(ps);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        packs= db1.readLimitPacks();
        packs1= db.readRestrictPacks();
        packs3=db3.readAllApps();
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(menuVisible && view!=null){
            packs= db1.readLimitPacks();
            packs1= db.readRestrictPacks();
            packs3=db3.readAllApps();
        }
    }
}