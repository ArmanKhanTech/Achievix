package com.android.achievix.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.achievix.Adapter.AppAdapter;
import com.android.achievix.Adapter.AppList;
import com.android.achievix.DataBase.JobDatabase;
import com.android.achievix.R;

import java.util.ArrayList;

public class JobReminderActivity extends AppCompatActivity {

    ArrayList<AppList> data=new ArrayList<>();
    AppAdapter adapter;
    ListView taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_reminder);

        ImageButton ib1=findViewById(R.id.imageButton6);
        ib1.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),NewJobActivity.class)));

        Button b=findViewById(R.id.back);
        b.setOnClickListener(view -> finish());

        taskList=findViewById(R.id.taskList);

        JobDatabase db=new JobDatabase(this);
        ArrayList<String> tasks=db.readTasks();

        for(int i=0;i<tasks.size();i++){
            @SuppressLint("UseCompatLoadingForDrawables") Drawable icon=getDrawable(R.drawable.task_icon);
            String temp=tasks.get(i);
            data.add(new AppList(temp,icon));
        }

        adapter=new AppAdapter(this,data);
        taskList.setAdapter(adapter);

        TextView tv=findViewById(R.id.twx);

        if(db.isDbEmpty()){
            tv.setText(" ");
        }
        else{
            tv.setText("No Task At The Moment");
        }

        taskList.setOnItemClickListener((parent, view, position, id) -> {
            AppList taskList = adapter.getItem(position);
            String task=taskList.getName();
            LayoutInflater layoutInflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View popupView = layoutInflater.inflate(R.layout.task_popup, null);
            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            TextView title=popupView.findViewById(R.id.textView9);
            TextView date=popupView.findViewById(R.id.textView10);
            TextView time=popupView.findViewById(R.id.textView11);
            Button bb=popupView.findViewById(R.id.bbb123);
            String d=db.readDate(task);
            String t=db.readTime(task);
            title.setText(task);
            date.setText(d);
            time.setText(t);
            bb.setOnClickListener(view1 -> {
                db.deleteTask(task);
                popupWindow.dismiss();
            });
            dimBehind(popupWindow);
        });

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
    protected void onResume() {
        super.onResume();
    }
}