package com.android.achievix.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.achievix.R;

public class ParentalControlActivity extends AppCompatActivity {

    EditText edt1 ;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch s1;
    Button b1,b2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parental_control);
        LinearLayout lll=findViewById(R.id.lll);
        s1=findViewById(R.id.sw1);
        s1.setChecked(checkPass());

        b2=findViewById(R.id.bbb123);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(!checkPass()){
            lll.setVisibility(View.GONE);
        }
        else{
            lll.setVisibility(View.VISIBLE);
        }

        s1.setOnCheckedChangeListener((compoundButton, b) -> {
            if(!b){
                int pass=0;
                SharedPreferences.Editor editor = getSharedPreferences("PASS_CODE", MODE_PRIVATE).edit();
                editor.putInt("pass", pass);
                editor.apply();
                lll.setVisibility(View.GONE);
            }
            else{
                lll.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Kindly Enter A New Password.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean checkPass(){
        SharedPreferences sh = getSharedPreferences("PASS_CODE", Context.MODE_PRIVATE);
        boolean is;
        int i = sh.getInt("pass",0 );
        is= i > 0;
        return is;
    }

    public void showPopup(View v){
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = layoutInflater.inflate(R.layout.new_pass_popup, null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        edt1 = popupView.findViewById(R.id.newpass);
        b1=popupView.findViewById(R.id.b1);
        b1.setOnClickListener(view -> {
            String temp=edt1.getText().toString();
            if(temp.isEmpty()){
                Toast.makeText(getApplicationContext(), "Feild Cannot Be Empty", Toast.LENGTH_SHORT).show();
            }
            else if(temp.length()<=4){
                Toast.makeText(getApplicationContext(), "Password Too Short", Toast.LENGTH_SHORT).show();
            }
            else {
                int pass = Integer.parseInt(temp);
                SharedPreferences.Editor editor = getSharedPreferences("PASS_CODE", MODE_PRIVATE).edit();
                editor.putInt("pass", pass);
                editor.apply();
                Toast.makeText(getApplicationContext(), "Password Set", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }
        });
        dimBehind(popupWindow);
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