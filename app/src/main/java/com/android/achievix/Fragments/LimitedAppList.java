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

import com.android.achievix.Activity.LimitActivity;
import com.android.achievix.Adapter.AppAdapter;
import com.android.achievix.Adapter.AppList;
import com.android.achievix.Database.AnalysisDatabase;
import com.android.achievix.Database.Packages;
import com.android.achievix.Database.LimitPackages;
import com.android.achievix.Database.RestrictPackages;
import com.android.achievix.R;
import com.android.achievix.Service.ForegroundService;

import java.util.ArrayList;

public class LimitedAppList extends Fragment {


    ListView lvv1;
    TextView tvvv1;
    private LimitPackages db;
    private RestrictPackages db2;
    private Packages db3;
    private AnalysisDatabase db4;
    private ArrayList<String> packs,packs2;
    private ArrayList<AppList> installedApps,limitedAppList;
    private AppAdapter installedAppAdapter;
    private Button unlimit;
    private final ArrayList<String> packages=new ArrayList<>();
    LinearLayout ll2;
    loadApps la=new loadApps();
    View view;
    Boolean b=false;

    public LimitedAppList() {
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
        view=inflater.inflate(R.layout.fragment_limited_app_list,container,false);
        lvv1= view.findViewById(R.id.lvv1);
        tvvv1= view.findViewById(R.id.tvvv1);
        ll2=view.findViewById(R.id.ll2);
        unlimit=view.findViewById(R.id.unlimit);
        unlimit.setVisibility(View.GONE);
        db=new LimitPackages(getActivity());
        db2=new RestrictPackages(getActivity());
        db3=new Packages(getActivity());
        db4=new AnalysisDatabase(getActivity());
        packs=db.readLimitPacks();
        packs2=db3.readPacks();

        if(!packs.isEmpty()){
            tvvv1.setText("");
        }
        else{
            tvvv1.setText("No Limited App.");
        }

      lvv1.setOnItemClickListener((adapterView, view12, i, l) -> {
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

        unlimit.setOnClickListener(view1 -> {
            for(int i=0;i<packages.size();i++){
                String ps= packages.get(i);
                db.deleteLimitPack(ps);
                db3.deletePackage(ps);
                db4.inAppUnlimit(ps);
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
            unlimit.setBackground(getResources().getDrawable(R.drawable.app_button_theme));
        }
        else{
            unlimit.setBackground(getResources().getDrawable(R.drawable.list_bg_theme));
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class loadApps extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ll2.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            installedApps = ((LimitActivity) requireActivity()).getInstalledApps();
            return null;
        }

        @Override
        protected void onPostExecute(Void results) {
            super.onPostExecute(results);
            limitedAppList=getList();
            installedAppAdapter = new AppAdapter(requireActivity(),limitedAppList);
            lvv1.setAdapter(installedAppAdapter);
            installedAppAdapter.notifyDataSetChanged();
            ll2.setVisibility(View.GONE);
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
        packs=db.readLimitPacks();
        limitedAppList=getList();
        installedAppAdapter = new AppAdapter(requireActivity(),limitedAppList);
        lvv1.setAdapter(installedAppAdapter);
        installedAppAdapter.notifyDataSetChanged();

        if(!packs.isEmpty()){
            tvvv1.setText("");
        }
        else{
            tvvv1.setText("No Limited App.");
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

