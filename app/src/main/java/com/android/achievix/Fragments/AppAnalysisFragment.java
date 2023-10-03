package com.android.achievix.Fragments;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.achievix.Adapter.AppAdapter;
import com.android.achievix.Adapter.AppList;
import com.android.achievix.Database.AnalysisDatabase;
import com.android.achievix.R;

import java.util.ArrayList;

public class AppAnalysisFragment extends Fragment {

    AnalysisDatabase db;
    ArrayList<String> apps=new ArrayList<>();
    ArrayList<AppList> aa=new ArrayList<>();
    ListView l1;
    AppAdapter a1;

    public AppAnalysisFragment() {
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_app_analysis, container, false);
        l1=view.findViewById(R.id.app_ana);
        TextView t1=view.findViewById(R.id.tvvv123);
        db=new AnalysisDatabase(getActivity());
        apps=db.readAllApps();

        if(apps.isEmpty()){
            t1.setText("Nothing to Show");
        }else{
            for(int i=0;i< apps.size();i++){
                String pkname=apps.get(i);
                PackageManager pm= requireActivity().getPackageManager();
                String appname="";
                try {
                    appname= (String) pm.getApplicationLabel(pm.getApplicationInfo(pkname,PackageManager.GET_META_DATA));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                Drawable icon = null ;
                try {
                    icon = requireActivity().getPackageManager().getApplicationIcon(pkname);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if(pkname!=null) {
                    aa.add(new AppList(appname, icon, pkname));
                }
            }
        }

        a1 = new AppAdapter(requireActivity(),aa);
        l1.setAdapter(a1);

        l1.setOnItemClickListener((adapterView, v, i, l) -> {
            AppList appList= a1.getItem(i);
            String a=appList.getName();
            String p=appList.getPackageName();
            LayoutInflater layoutInflater = (LayoutInflater) requireContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View popupView = layoutInflater.inflate(R.layout.analysis_app_popup, null);
            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            dimBehind(popupWindow);
            TextView t112 =popupView.findViewById(R.id.taa1);
            TextView t212 =popupView.findViewById(R.id.ta11);
            TextView t3=popupView.findViewById(R.id.ta21);
            TextView t4=popupView.findViewById(R.id.ta31);
            TextView t5=popupView.findViewById(R.id.ta41);
            TextView t6=popupView.findViewById(R.id.ta51);
            TextView t7=popupView.findViewById(R.id.ta61);
            t112.setText(a);
            t212.setText(db.readAppRestricted(p));
            t3.setText(db.readAppUnrestricted(p));
            t4.setText(db.readAppLimited(p));
            t5.setText(db.readAppUnlimited(p));
            t6.setText(db.readAppBlocked(p));
            t7.setText(db.readAppNotiBlocked(p));
        });
        return view;
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