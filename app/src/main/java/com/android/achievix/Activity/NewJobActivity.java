package com.android.achievix.Activity;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.achievix.DataBase.JobDatabase;
import com.android.achievix.R;
import com.android.achievix.Service.AlarmBoardcast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class NewJobActivity extends AppCompatActivity {

    Button mSubmitbtn, mDatebtn, mTimebtn;
    EditText mTitledit, mDescdit;
    String timeTonotify;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_job);

        mTitledit = findViewById(R.id.editTitle);
        mDescdit = findViewById(R.id.editText);
        mDatebtn = findViewById(R.id.button2);
        mTimebtn = findViewById(R.id.button);
        mSubmitbtn = findViewById(R.id.submit);
        Button bb=findViewById(R.id.b12347www);

        bb.setOnClickListener(view -> finish());

        mTimebtn.setOnClickListener(view -> selectTime());

        mDatebtn.setOnClickListener(view -> selectDate());

        mSubmitbtn.setOnClickListener(view -> {
            String title = mTitledit.getText().toString().trim();
            String date = mDatebtn.getText().toString().trim();
            String time = mTimebtn.getText().toString().trim();
            String desc = mTimebtn.getText().toString().trim();
            if (title.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please Enter Title", Toast.LENGTH_SHORT).show();
            }
            else if(desc.isEmpty()){
                Toast.makeText(getApplicationContext(), "Please Enter Description", Toast.LENGTH_SHORT).show();
            }
            else {
                if (time.equals("time") || date.equals("date")) {
                    Toast.makeText(getApplicationContext(), "Please Select Date and Time", Toast.LENGTH_SHORT).show();
                } else {
                    new JobDatabase(getApplicationContext()).addReminder(title, desc, date, time);
                    setAlarm(title, date, time);
                    finish();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void selectTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.PickerTheme, (timePicker, i, i1) -> {
            timeTonotify = i + ":" + i1;
            mTimebtn.setText(FormatTime(i, i1));
        }, hour, minute, false);
        timePickerDialog.show();
        timePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.pink));
        timePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.pink));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor")
    private void selectDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.PickerTheme, (datePicker, year1, month1, day1) -> mDatebtn.setText(day1 + "-" + (month1 + 1) + "-" + year1), year, month, day);
        datePickerDialog.show();
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.pink));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.pink));
    }

    public String FormatTime(int hour, int minute) {
        String time;
        String formattedMinute;
        if (minute / 10 == 0) {
            formattedMinute = "0" + minute;
        } else {
            formattedMinute = "" + minute;
        }
        if (hour == 0) {
            time = "12" + ":" + formattedMinute + " AM";
        } else if (hour < 12) {
            time = hour + ":" + formattedMinute + " AM";
        } else if (hour == 12) {
            time = "12" + ":" + formattedMinute + " PM";
        } else {
            int temp = hour - 12;
            time = temp + ":" + formattedMinute + " PM";
        }
        return time;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setAlarm(String text, String date, String time) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmBoardcast.class);
        intent.putExtra("event", text);
        intent.putExtra("time", date);
        intent.putExtra("date", time);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, FLAG_IMMUTABLE);
        String dateandtime = date + " " + timeTonotify;

        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("d-M-yyyy hh:mm");
        try {
            Date date1 = formatter.parse(dateandtime);
            am.set(AlarmManager.RTC_WAKEUP, Objects.requireNonNull(date1).getTime(), pendingIntent);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        Intent intentBack = new Intent(getApplicationContext(), MainActivity.class);
        intentBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentBack);
    }
}