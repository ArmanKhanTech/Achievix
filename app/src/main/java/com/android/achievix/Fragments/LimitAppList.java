package com.android.achievix.Fragments;
//DONE

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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

import com.android.achievix.Activity.LimitActivity;
import com.android.achievix.Activity.LimitAppActivity;
import com.android.achievix.Adapter.AppAdapter;
import com.android.achievix.Adapter.AppList;
import com.android.achievix.Database.AnalysisDatabase;
import com.android.achievix.Database.Packages;
import com.android.achievix.Database.LimitPackages;
import com.android.achievix.Database.RestrictPackages;
import com.android.achievix.R;
import com.android.achievix.Service.ForegroundService;

import java.util.ArrayList;

public class LimitAppList extends Fragment  {

    private ArrayList<AppList> installedApps,limitInstalledApps;
    private AppAdapter installedAppAdapter;
    private ListView userInstalledApps;
    private ProgressBar pBar;
    private TextView pw2;
    private ArrayList<String> packs,packs1,packs2,packs3;
    private Button b1;
    private final ArrayList<String> packages=new ArrayList<>();
    LimitPackages db;
    RestrictPackages db1;
    Packages db2;
    AnalysisDatabase db3;
    loadApps la=new loadApps();
    View view;
    FrameLayout fll1;

    public LimitAppList() {
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
        view = inflater.inflate(R.layout.fragment_limit_app_list, container, false);
        userInstalledApps = view.findViewById(R.id.installed_app_list_limit);
        EditText editText = view.findViewById(R.id.search_bar);
        pBar= view.findViewById(R.id.pBar1);
        pw2= view.findViewById(R.id.pw2);
        b1=view.findViewById(R.id.limit);
        b1.setVisibility(View.GONE);
        fll1=view.findViewById(R.id.fll1);
        fll1.setVisibility(View.GONE);
        db = new LimitPackages(getActivity());
        db1 = new RestrictPackages(getActivity());
        db2= new Packages(getActivity());
        db3=new AnalysisDatabase(getActivity());
        packs= db.readLimitPacks();
        packs1= db1.readRestrictPacks();
        packs2= db2.readPacks();
        packs3= db3.readAllApps();

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
                Toast.makeText(getActivity(),appName+" Already Limited." ,Toast.LENGTH_SHORT).show();
            }
            else if(packageName.equals("com.android.focusonme")){
                Toast.makeText(getActivity(),appName+" Cannot Be Limited." ,Toast.LENGTH_SHORT).show();
            }
            else if(packs1.contains(packageName)){
                Toast.makeText(getActivity(),appName+" Already Restricted." ,Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(),appName+" Already Limited." ,Toast.LENGTH_SHORT).show();
            }
            else if(packageName.equals("com.android.focusonme")){
                Toast.makeText(getActivity(),appName+" Cannot Be Limited." ,Toast.LENGTH_SHORT).show();
            }
            else if(packs1.contains(packageName)){
                Toast.makeText(getActivity(),appName+" Already Restricted." ,Toast.LENGTH_SHORT).show();
            }
            else{
                Intent intent = new Intent(getActivity(), LimitAppActivity.class);
                intent.putExtra("APP_NAME", appName);
                intent.putExtra("PACKAGE_NAME", packageName);
                startActivity(intent);
            }
            return true;
        });

        b1.setOnClickListener(view1 -> {
            LayoutInflater layoutInflater = (LayoutInflater)
                    requireActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View popupView = layoutInflater.inflate(R.layout.popup_limit, null);
            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
            if(packages.size()>0) {
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                dimBehind(popupWindow);
                Button b2= popupView.findViewById(R.id.ll);
                EditText hrs= popupView.findViewById(R.id.hrs);
                EditText mins= popupView.findViewById(R.id.mins);
                b2.setOnClickListener(view2 -> {
                    if(TextUtils.isEmpty(hrs.getText().toString())){
                        Toast.makeText(getActivity(), "Field Cannot Be Empty", Toast.LENGTH_SHORT).show();
                    }
                    else if(TextUtils.isEmpty(mins.getText().toString())) {
                        Toast.makeText(getActivity(), "Field Cannot Be Empty", Toast.LENGTH_SHORT).show();
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
                        for (int i = 0; i < packages.size(); i++) {
                            String ps = packages.get(i);
                            db.addNewPackageInLimit(ps, duration);
                            db2.deletePackage(ps);
                            if(!packs3.contains(ps)){
                                db3.addApp(ps);
                            }
                            else{
                                db3.inAppLimit(ps);
                            }
                        }
                        packages.clear();
                        setButtonColor();
                        updateList();
                        startService();
                        popupWindow.dismiss();
                    }
                });
            }
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
            b1.setBackground(getResources().getDrawable(R.drawable.limit_button_theme));
        }
        else{
            b1.setBackground(getResources().getDrawable(R.drawable.list_bg_theme));
        }
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
            installedApps = ((LimitActivity) requireActivity()).getInstalledApps();
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
        packs= db.readLimitPacks();
        packs1= db1.readRestrictPacks();
        packs3=db3.readAllApps();
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
        packs= db.readLimitPacks();
        packs1= db1.readRestrictPacks();
        packs3= db3.readAllApps();
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(menuVisible && view!=null){
            packs= db.readLimitPacks();
            packs1= db1.readRestrictPacks();
            packs3= db3.readAllApps();
        }
    }
}