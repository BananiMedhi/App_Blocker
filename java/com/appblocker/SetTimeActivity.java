package com.appblocker;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by banani on 5/30/2016.
 */
public class SetTimeActivity extends Activity implements View.OnClickListener{

    TimePicker tpStartTime, tpEndTime;
    Button btnSubmit, btnCancel ;
    AlarmManager alarmManager;
    Context context;
    SharedPreferences sharedPreferences,spref;
    String appName;
    AppDatabase appDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_duration);

        context=SetTimeActivity.this;

        tpStartTime = (TimePicker) findViewById(R.id.startTime);
        tpEndTime = (TimePicker) findViewById(R.id.endTime);
        sharedPreferences = getSharedPreferences("MyPreferences",MODE_PRIVATE);
        spref = getSharedPreferences("TickPreferences", MODE_PRIVATE);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);


        Bundle bun = getIntent().getExtras();
        if(bun!=null) {
            appName = bun.getString("AppName");
        }
         appDatabase = new AppDatabase(context);
    }




    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.btnSubmit:
            {
                int startHour = tpStartTime.getCurrentHour();
                int startMin  = tpStartTime.getCurrentMinute();


                int endHour = tpEndTime.getCurrentHour();
                int endMin  = tpEndTime.getCurrentMinute();

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY,startHour);
                cal.set(Calendar.MINUTE,startMin);
                cal.set(Calendar.SECOND,0);

                long startTime  = cal.getTimeInMillis();

                Calendar cal2 = Calendar.getInstance();
                cal2.set(Calendar.HOUR_OF_DAY,endHour);
                cal2.set(Calendar.MINUTE,endMin);
                cal2.set(Calendar.SECOND,0);

                long endTime  = cal2.getTimeInMillis();

                if(endTime <= startTime){
                    Toast.makeText(context,"You have entered incorrect time. Please try again",Toast.LENGTH_LONG).show();
                    break;
                }
                else{
/*
                alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent in = new Intent(context, BlockingActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent alarmIntent = PendingIntent.getActivity(context, PendingIntent.FLAG_UPDATE_CURRENT, in, 0);
                alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), alarmIntent);*/


               /* SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("AppName", appName);
                editor.putLong("start", startTime);
                editor.putLong("end", endTime);
                editor.commit();*/

                if(spref.getBoolean(appName,false)){
                    appDatabase.updateApps(appName, startTime, endTime);
                }
                    else {
                    appDatabase.insertApps(appName, startTime, endTime);
                }

                    View coordinatorLayout = findViewById(R.id.snackbar_position);
                   /* Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Changes saved successfully", Snackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(Color.parseColor("#FFFF00"));
                    snackbar.show();*/
                Toast.makeText(context,"Changes saved successfully",Toast.LENGTH_LONG).show();

               // map.put(appName, true);


                Log.i("appname", appName);
                SharedPreferences.Editor ed = spref.edit();
                ed.putBoolean(appName,true);
                ed.apply();
                finish();

                Intent i = new Intent(context,ListViewActivity.class);
                startActivity(i);
              //  Log.i("AppInfoModel", String.valueOf(map.get(appName)));

            }}
            break;
            case R.id.btnCancel:{
                onBackPressed();
            }break;
            default:

        }
    }
}
