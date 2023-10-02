package com.android.achievix.Fragments;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.achievix.Activity.InternetBlockActivity;
import com.android.achievix.Adapter.AppAdapter;
import com.android.achievix.Adapter.AppList;
import com.android.achievix.DataBase.InternetBlockDatabase;
import com.android.achievix.DataBase.Packages;
import com.android.achievix.R;
import com.android.achievix.Service.ForegroundService;

import java.util.ArrayList;

public class DataLimitAppList extends Fragment {

    private ArrayList<AppList> installedApps,limitInstalledApps;
    private AppAdapter installedAppAdapter;
    private ListView userInstalledApps;
    private ProgressBar pBar;
    private TextView pw2;
    private ArrayList<String> packs1,packs2;
    private Button b1;
    private final ArrayList<String> packages=new ArrayList<>();
    InternetBlockDatabase db1;
    Packages db2;
    loadApps la=new loadApps();
    View view;
    FrameLayout fll1;

    public DataLimitAppList() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        la.execute();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_data_limit_app_list, container, false);
        userInstalledApps = view.findViewById(R.id.installed_app_list_data);
        EditText editText = view.findViewById(R.id.search_bar1);
        pBar= view.findViewById(R.id.pBar11);
        pw2= view.findViewById(R.id.pw22);
        b1=view.findViewById(R.id.limit123);
        b1.setVisibility(View.GONE);
        fll1=view.findViewById(R.id.fll11);
        fll1.setVisibility(View.GONE);

        db1=new InternetBlockDatabase(getActivity());
        db2= new Packages(getActivity());

        packs1= db1.readInternetPacks();
        packs2= db2.readPacks();

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
            String packageName= appList.getPackageName();
            String appName=appList.getName();
            if(packageName.equals("com.android.focusonme")){
                Toast.makeText(getActivity(),"Data Usage Of This App Cannot Be Limited." ,Toast.LENGTH_SHORT).show();
            }
            else if(packs1.contains(packageName)){
                Toast.makeText(getActivity(),"Data Usage Of This App Is Already Limited." , Toast.LENGTH_SHORT).show();
            }
            else{
                if(packs2.contains(packageName)){
                    cs.setBackground(getResources().getDrawable(R.drawable.listview_theme));
                    db2.deletePackage(packageName);
                    packs2 = db2.readPacks();
                    packages.remove(packageName);
                }
                else {
                    cs.setBackground(getResources().getDrawable(R.drawable.listview_select_theme));
                    db2.addPackage(packageName);
                    packages.add(packageName);
                    packs2 = db2.readPacks();
                }
            }
            installedAppAdapter.notifyDataSetChanged();
        });

        b1.setOnClickListener(view1 -> {
            LayoutInflater layoutInflater = (LayoutInflater)
                    requireActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View popupView = layoutInflater.inflate(R.layout.popup_getdata, null);
            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
            if(packages.size()>0) {
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                dimBehind(popupWindow);
                Button b2= popupView.findViewById(R.id.llll);
                EditText data= popupView.findViewById(R.id.data);
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String temp=data.getText().toString();
                        if(!temp.isEmpty()) {
                            for (int i = 0; i < packages.size(); i++) {
                                String ps = packages.get(i);
                                db1.addNewPackageInInternet(ps,temp);
                                db2.deletePackage(ps);
                            }
                            packages.clear();
                            updateList();
                            startService();
                            popupWindow.dismiss();
                        }
                        else{
                            Toast.makeText(getActivity(),"Feild Cannot Be Empty." ,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else{
                Toast.makeText(getActivity(),"Please Select Apps." ,Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void startService() {
        Intent serviceIntent = new Intent(getActivity(), ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service is Running");
        ContextCompat.startForegroundService(requireActivity(), serviceIntent);
    }

    @SuppressLint("StaticFieldLeak")
    private class loadApps extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pw2.setVisibility(View.VISIBLE);
            pBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            installedApps = ((InternetBlockActivity) requireActivity()).getInstalledApps();
            return null;
        }

        @Override
        protected void onPostExecute(Void results) {
            super.onPostExecute(results);
            pw2.setVisibility(View.GONE);
            pBar.setVisibility(View.GONE);
            b1.setVisibility(View.VISIBLE);
            fll1.setVisibility(View.VISIBLE);
            limitInstalledApps=installedApps;
            installedAppAdapter = new AppAdapter(requireActivity(),limitInstalledApps);
            userInstalledApps.setAdapter(installedAppAdapter);
            installedAppAdapter.notifyDataSetChanged();
            cancel(true);
        }
    }

    public void updateList(){
        packs1= db1.readInternetPacks();
        installedAppAdapter = new AppAdapter(requireActivity(),limitInstalledApps);
        userInstalledApps.setAdapter(installedAppAdapter);
        installedAppAdapter.notifyDataSetChanged();
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
        packs1= db1.readInternetPacks();
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(menuVisible && view!=null){
            packs1= db1.readInternetPacks();
        }
    }
}