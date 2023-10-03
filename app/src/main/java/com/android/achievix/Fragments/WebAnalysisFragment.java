package com.android.achievix.Fragments;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
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

public class WebAnalysisFragment extends Fragment {

    AnalysisDatabase db;
    ArrayList<String> webs=new ArrayList<>();
    ArrayList<AppList> ww=new ArrayList<>();
    ListView l2;
    AppAdapter a2;

    public WebAnalysisFragment() {
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_web_analysis, container, false);
        l2=view.findViewById(R.id.web_ana);
        TextView t2=view.findViewById(R.id.tvvv1234);
        db=new AnalysisDatabase(getActivity());
        webs=db.readAllAWeb();

        if(webs.isEmpty()){
            t2.setText("Nothing to Show");
        }
        else{
            for(int i=0;i< webs.size();i++){
                @SuppressLint("UseCompatLoadingForDrawables") Drawable icon= requireContext().getDrawable(R.drawable.web_icon);
                String w=webs.get(i);
                if(w!=null) {
                    ww.add(new AppList(w, icon, ""));
                }
            }
        }

        a2 = new AppAdapter(requireActivity(),ww);
        l2.setAdapter(a2);

        l2.setOnItemClickListener((adapterView, v, i, l) -> {
            AppList appList= a2.getItem(i);
            String w=appList.getName();
            LayoutInflater layoutInflater = (LayoutInflater) requireContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View popupView = layoutInflater.inflate(R.layout.analysis_web_popup, null);
            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            dimBehind(popupWindow);
            TextView t11 =popupView.findViewById(R.id.taa);
            TextView t21 =popupView.findViewById(R.id.ta1);
            TextView t3=popupView.findViewById(R.id.ta2);
            TextView t4=popupView.findViewById(R.id.ta3);
            t11.setText(w);
            t21.setText(db.readWebBlocked(w));
            t3.setText(db.readWebUnlocked(w));
            t4.setText(db.readAccessDenied(w));
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