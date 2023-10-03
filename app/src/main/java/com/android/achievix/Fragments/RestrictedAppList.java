package com.android.achievix.Fragments;
//DONE
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.android.achievix.Activity.RestrictActivity;
import com.android.achievix.Adapter.AppAdapter;
import com.android.achievix.Adapter.AppList;
import com.android.achievix.Database.AnalysisDatabase;
import com.android.achievix.Database.Packages;
import com.android.achievix.Database.LimitPackages;
import com.android.achievix.Database.RestrictPackages;
import com.android.achievix.R;
import com.android.achievix.Service.ForegroundService;

import java.util.ArrayList;

public class RestrictedAppList extends Fragment {

    ListView lvv2;
    TextView tvvv2;
    private RestrictPackages db;
    private LimitPackages db2;
    private Packages db3;
    private AnalysisDatabase db4;
    private ArrayList<String> packs,packs2;
    private ArrayList<AppList> installedApps;
    private ArrayList<AppList> restrictedAppList;
    private AppAdapter installedAppAdapter;
    private Button unrestrict;
    private final ArrayList<String> packages=new ArrayList<>();
    LinearLayout ll1;
    loadApps la=new loadApps();
    Boolean b=false;
    View view;

    public RestrictedAppList() {
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
        view=inflater.inflate(R.layout.fragment_restricted_app_list,container,false);
        lvv2= view.findViewById(R.id.lvv2);
        tvvv2= view.findViewById(R.id.tvvv2);
        ll1=view.findViewById(R.id.ll1);
        unrestrict=view.findViewById(R.id.unrestrict);
        unrestrict.setVisibility(View.GONE);
        db=new RestrictPackages(getActivity());
        db2=new LimitPackages(getActivity());
        db3=new Packages(getActivity());
        db4=new AnalysisDatabase(getActivity());
        packs=db.readRestrictPacks();
        packs2=db3.readPacks();

        if(!packs.isEmpty()){
            tvvv2.setText("");
        }
        else{
            tvvv2.setText("No Restricted App.");
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
               setButtonColor();
           }
           else {
               cs.setBackground(getResources().getDrawable(R.drawable.listview_select_theme));
               db3.addPackage(packageName);
               packages.add(packageName);
               packs2 = db3.readPacks();
               setButtonColor();
           }
           installedAppAdapter.notifyDataSetChanged();
        });

        unrestrict.setOnClickListener(view1 -> {
            for(int i=0;i<packages.size();i++){
                String ps= packages.get(i);
                db.deleteRestrictPack(ps);
                db3.deletePackage(ps);
                db4.inAppUnrestrict(ps);
            }
            packages.clear();
            setButtonColor();
            updateList();
            stopService();
        });

        return view;
    }

    public void setButtonColor(){
        if(packages.size()>0){
            unrestrict.setBackground(getResources().getDrawable(R.drawable.restrictbutton_theme));
        }
        else{
            unrestrict.setBackground(getResources().getDrawable(R.drawable.bgbutton_theme));
        }
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
            installedApps = ((RestrictActivity) requireActivity()).getInstalledApps();
            return null;
        }

        @Override
        protected void onPostExecute(Void results) {
            super.onPostExecute(results);
            restrictedAppList=getList();
            installedAppAdapter = new AppAdapter(requireActivity(),restrictedAppList);
            lvv2.setAdapter(installedAppAdapter);
            installedAppAdapter.notifyDataSetChanged();
            ll1.setVisibility(View.GONE);
            unrestrict.setVisibility(View.VISIBLE);
            b=true;
        }
    }

    private ArrayList<AppList> getList(){
        ArrayList<AppList> filters = new ArrayList<>();
        int a,b;
        for (a = 0; a < installedApps.size(); a++) {
            for(b = 0;b < packs.size(); b++) {
                if (installedApps.get(a).getPackageName().contains(packs.get(b))) {
                    AppList appList = new AppList(installedApps.get(a).getName(), installedApps.get(a).getPackageName(), installedApps.get(a).getIcon());
                    filters.add(appList);
                }
            }
        }
        return filters;
    }

    public void stopService() {
        if(!db.isDbEmpty() && !db2.isDbEmpty()) {
            requireActivity().stopService(new Intent(getContext(), ForegroundService.class));
        }
    }

    @SuppressLint("SetTextI18n")
    public void updateList(){
        packs=db.readRestrictPacks();
        restrictedAppList=getList();
        installedAppAdapter = new AppAdapter(requireActivity(),restrictedAppList);
        lvv2.setAdapter(installedAppAdapter);
        installedAppAdapter.notifyDataSetChanged();

        if(!packs.isEmpty()){
            tvvv2.setText("");
        }
        else{
            tvvv2.setText("No Restricted App.");
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