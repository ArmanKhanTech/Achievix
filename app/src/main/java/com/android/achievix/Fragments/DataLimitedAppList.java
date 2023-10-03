package com.android.achievix.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.achievix.Activity.InternetBlockActivity;
import com.android.achievix.Adapter.AppAdapter;
import com.android.achievix.Adapter.AppList;
import com.android.achievix.Database.InternetBlockDatabase;
import com.android.achievix.Database.Packages;
import com.android.achievix.Database.LimitPackages;
import com.android.achievix.Database.RestrictPackages;
import com.android.achievix.R;
import com.android.achievix.Service.ForegroundService;

import java.util.ArrayList;

public class DataLimitedAppList extends Fragment {

    ListView lvv2;
    TextView tvvv2;
    private InternetBlockDatabase db;
    private Packages db3;
    private ArrayList<String> packs,packs2;
    private ArrayList<AppList> installedApps;
    private ArrayList<AppList> limitedAppList;
    private AppAdapter installedAppAdapter;
    private Button unlimit;
    private final ArrayList<String> packages=new ArrayList<>();
    LinearLayout ll1;
    loadApps la=new loadApps();
    Boolean b=false;
    View view;
    RestrictPackages db4;
    LimitPackages db5;

    public DataLimitedAppList() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        la.execute();
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_data_limited_app_list,container,false);
        lvv2= view.findViewById(R.id.lvv111);
        tvvv2= view.findViewById(R.id.tvvv11);
        ll1=view.findViewById(R.id.ll22);
        unlimit=view.findViewById(R.id.unlimit11);
        unlimit.setVisibility(View.GONE);

        db=new InternetBlockDatabase(getActivity());
        db3=new Packages(getActivity());
        db4=new RestrictPackages(getActivity());
        db5=new LimitPackages(getActivity());

        packs=db.readInternetPacks();
        packs2= db3.readPacks();

        if(!packs.isEmpty()){
            tvvv2.setText("");
        }
        else{
            tvvv2.setText("No Limited App.");
        }

        lvv2.setOnItemClickListener((adapterView, view12, i, l) -> {
            ConstraintLayout cs= view12.findViewById(R.id.ConstraintLayout);
            AppList appList = installedAppAdapter.getItem(i);
            String packageName= appList.getPackageName();
            if(packs2.contains(packageName)){
                cs.setBackground(getResources().getDrawable(R.drawable.listview_theme));
                db3.deletePackage(packageName);
                packs2 = db3.readPacks();
                packages.remove(packageName);
            }
            else {
                cs.setBackground(getResources().getDrawable(R.drawable.listview_select_theme));
                db3.addPackage(packageName);
                packages.add(packageName);
                packs2 = db3.readPacks();
            }
            installedAppAdapter.notifyDataSetChanged();
        });

        unlimit.setOnClickListener(view1 -> {
            if(packages.size()>0) {
                for (int i = 0; i < packages.size(); i++) {
                    String ps = packages.get(i);
                    db.deleteInternetPack(ps);
                    db3.deletePackage(ps);
                }
                packages.clear();
                updateList();
                stopService();
            }
            else{
                Toast.makeText(getActivity(),"Please Select Apps." ,Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    public class loadApps extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ll1.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            installedApps = ((InternetBlockActivity) requireActivity()).getInstalledApps();
            return null;
        }

        @Override
        protected void onPostExecute(Void results) {
            super.onPostExecute(results);
            limitedAppList=getList();
            installedAppAdapter = new AppAdapter(requireActivity(),limitedAppList);
            lvv2.setAdapter(installedAppAdapter);
            installedAppAdapter.notifyDataSetChanged();
            ll1.setVisibility(View.GONE);
            unlimit.setVisibility(View.VISIBLE);
            b=true;
        }
    }

    private ArrayList<AppList> getList(){
        ArrayList<AppList> filters = new ArrayList<>();
        int a,b;
        for (a = 0; a < installedApps.size(); a++) {
            for(b = 0;b < packs.size(); b++) {
                if (installedApps.get(a).getPackageName().contains(packs.get(b))) {
                    String data=db.readData(installedApps.get(a).getPackageName());
                    data = data + " MB";
                    AppList appList = new AppList(installedApps.get(a).getName(), installedApps.get(a).getPackageName(), installedApps.get(a).getIcon(),data);
                    filters.add(appList);
                }
            }
        }
        return filters;
    }

    public void stopService() {
        if(!db.isDbEmpty() && !db4.isDbEmpty() && !db5.isDbEmpty()) {
            requireActivity().stopService(new Intent(getContext(), ForegroundService.class));
        }
    }

    @SuppressLint("SetTextI18n")
    public void updateList(){
        packs=db.readInternetPacks();
        limitedAppList=getList();
        installedAppAdapter = new AppAdapter(requireActivity(),limitedAppList);
        lvv2.setAdapter(installedAppAdapter);
        installedAppAdapter.notifyDataSetChanged();

        if(!packs.isEmpty()){
            tvvv2.setText("");
        }
        else{
            tvvv2.setText("No Limited App.");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        la.cancel(true);
        for(int i=0;i<packages.size();i++){
            String ps=packages.get(i);
            db3.deletePackage(ps);
        }
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(menuVisible && view!=null && b){
            updateList();
        }
    }
}